package com.dbboys.vo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dbboys.util.GlobalErrorHandlerUtil;

/**
 * Holds runtime resources for an active database connection.
 * Pairs with a ConnectConfig (or Connect) to separate config from runtime state.
 */
public class ActiveSession implements AutoCloseable {
    private final ConnectConfig config;
    private Connection conn;
    private final ExecutorService executorService;
    private volatile boolean connected;

    public ActiveSession(ConnectConfig config) {
        this.config = config;
        this.executorService = Executors.newSingleThreadExecutor();
        this.connected = false;
    }

    public ActiveSession(ConnectConfig config, Connection conn) {
        this(config);
        this.conn = conn;
        this.connected = conn != null;
    }

    public ConnectConfig getConfig() { return config; }

    public Connection getConn() { return conn; }

    public void setConn(Connection conn) {
        this.conn = conn;
        this.connected = conn != null;
    }

    public boolean isConnected() { return connected; }

    public ExecutorService getExecutorService() { return executorService; }

    public void executeSqlTask(Thread thread) {
        executorService.submit(() -> {
            try {
                thread.run();
            } catch (Exception e) {
                GlobalErrorHandlerUtil.handle(e);
            }
        });
    }

    @Override
    public void close() {
        connected = false;
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
            conn = null;
        }
        executorService.shutdownNow();
    }
}
