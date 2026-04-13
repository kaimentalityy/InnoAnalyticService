package com.innowise.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ClickHouseServerNodes {
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
}


