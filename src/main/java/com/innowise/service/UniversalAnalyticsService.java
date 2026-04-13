package com.innowise.service;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.data.ClickHouseFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innowise.config.ClickHouseConfig;
import com.innowise.model.event.AnalyticsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UniversalAnalyticsService implements AutoCloseable {

    private final ClickHouseNode server;
    private final ObjectMapper clickhouseMapper;
    private final ClickHouseClient client;
    private final BlockingQueue<AnalyticsEvent> buffer = new LinkedBlockingQueue<>(10000);

    private static final int BATCH_SIZE = 5000;

    public UniversalAnalyticsService(ClickHouseConfig config) {
        this.clickhouseMapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(new JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var nodeConfig = config.getNodes().getFirst();
        this.server = ClickHouseNode.builder()
                .host(nodeConfig.getHost())
                .port(ClickHouseProtocol.HTTP, nodeConfig.getPort())
                .database(nodeConfig.getDatabase())
                .credentials(com.clickhouse.client.ClickHouseCredentials.fromUserAndPassword(
                        nodeConfig.getUsername(), nodeConfig.getPassword()))
                .build();
        this.client = ClickHouseClient.newInstance(ClickHouseProtocol.HTTP);
    }

    public void addToBuffer(AnalyticsEvent event) {
        if (!buffer.offer(event)) {
            log.warn("Buffer full, dropping event: {}", event.getEventId());
            return;
        }

        if (buffer.size() >= BATCH_SIZE) {
            CompletableFuture.runAsync(this::flush);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduledFlush() {
        if (!buffer.isEmpty()) {
            log.info("Triggering scheduled flush for {} events", buffer.size());
            flush();
        }
    }

    public synchronized void flush() {
        if (buffer.isEmpty())
            return;

        List<AnalyticsEvent> batch = new ArrayList<>();
        buffer.drainTo(batch, BATCH_SIZE);

        try {
            Map<String, List<AnalyticsEvent>> eventsByTable = batch.stream()
                    .collect(Collectors.groupingBy(AnalyticsEvent::getTableName));

            for (Map.Entry<String, List<AnalyticsEvent>> entry : eventsByTable.entrySet()) {
                String tableName = entry.getKey();
                List<AnalyticsEvent> tableEvents = entry.getValue();
                flushToTable(tableName, tableEvents);
            }

        } catch (Exception e) {
            log.error("Failed to flush to ClickHouse. Potential data loss!", e);
        }
    }

    private void flushToTable(String tableName, List<AnalyticsEvent> events) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (SequenceWriter writer = clickhouseMapper.writer().withRootValueSeparator("\n").writeValues(out)) {
            for (AnalyticsEvent event : events) {
                writer.write(event);
            }
        }
        byte[] jsonPayload = out.toByteArray();
        log.debug("Flushing batch of {} events to table {}: {}", events.size(), tableName, new String(jsonPayload));

        client.read(server)
                .write()
                .table(tableName)
                .format(ClickHouseFormat.JSONEachRow)
                .set("input_format_skip_unknown_fields", 1)
                .data(new ByteArrayInputStream(jsonPayload))
                .execute()
                .thenAccept(response -> log.info("Flushed {} rows to ClickHouse table {}", events.size(), tableName))
                .exceptionally(ex -> {
                    log.error("Failed to flush {} rows to ClickHouse table {}: {}", events.size(), tableName,
                            ex.getMessage(), ex);
                    return null;
                });
    }

    @Override
    public void close() {
        client.close();
    }
}
