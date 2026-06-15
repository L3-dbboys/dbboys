package com.dbboys.infra.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Shared SQLite connection for local persistence (dbboys.dat).
 */
public final class LocalDbConnection {
    private static final String DB_PATH = "jdbc:sqlite:data/dbboys.dat";
    private static final Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize local SQLite connection", e);
        }
    }

    private LocalDbConnection() {}

    public static Connection get() {
        return conn;
    }
}
