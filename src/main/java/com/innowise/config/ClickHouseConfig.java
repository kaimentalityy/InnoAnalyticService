package com.innowise.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "clickhouse")
@Getter
@Setter
public class ClickHouseConfig {

    private List<ClickHouseServerNodes> nodes;

}
