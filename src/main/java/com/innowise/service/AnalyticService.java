package com.innowise.service;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.data.ClickHouseFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.innowise.config.ClickHouseConfig;
import com.innowise.model.event.UserLifecycleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class AnalyticService implements AutoCloseable {

    private final ClickHouseNode server;
    private final ObjectMapper objectMapper;
    private final ClickHouseClient client;
    private final BlockingQueue<UserLifecycleEvent> buffer = new LinkedBlockingQueue<>(10000);

    private static final int BATCH_SIZE = 5000;

    public AnalyticService(ClickHouseConfig config) {
        this.objectMapper = new ObjectMapper();
        var nodeConfig = config.getNodes().getFirst();
        this.server = ClickHouseNode.of(nodeConfig.getHost(), ClickHouseProtocol.HTTP, nodeConfig.getPort(), "default");
        this.client = ClickHouseClient.newInstance(ClickHouseProtocol.HTTP);
    }

    public void addToBuffer(UserLifecycleEvent event) {
        if (!buffer.offer(event)) {
            log.warn("Buffer full, dropping event: {}", event.eventId());
            return;
        }

        if (buffer.size() >= BATCH_SIZE) {
            CompletableFuture.runAsync(this::flush);
        }
    }

    public synchronized void flush() {
        if (buffer.isEmpty()) return;

        List<UserLifecycleEvent> batch = new ArrayList<>();
        buffer.drainTo(batch, BATCH_SIZE);

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (SequenceWriter writer = objectMapper.writer().withRootValueSeparator("\n").writeValues(out)) {
                for (UserLifecycleEvent event : batch) {
                    writer.write(event);
                }
            }
            byte[] jsonPayload = out.toByteArray();

            client.read(server)
                    .write()
                    .table("user_lifecycle_events")
                    .format(ClickHouseFormat.JSONEachRow)
                    .data(new ByteArrayInputStream(jsonPayload))
                    .execute()
                    .thenAccept(response -> log.info("Flushed {} rows", batch.size()));

        } catch (Exception e) {
            log.error("Failed to flush to ClickHouse. Potential data loss!", e);
        }
    }

    @Override
    public void close() {
        client.close();
    }
}
