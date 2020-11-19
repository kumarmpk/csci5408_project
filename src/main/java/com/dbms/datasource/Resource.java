package com.dbms.datasource;

import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public final class Resource {
    final String dbPath = Paths.get("").toAbsolutePath().toString() + "\\data\\";
}
