package com.dbboys.vo;

import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.Future;

public class BackgroundSqlTask {
    private String id;
    private String beginTime;
    private String connectName;
    private String databaseName;
    private String sql;
    private String operate;
    private Connect connect;
    private Statement stmt;
    private Future<?> future;
    private volatile boolean cancelled;
    public BackgroundSqlTask(){
        id= UUID.randomUUID().toString();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getConnectName() {
        return connectName;
    }

    public void setConnectName(String connectName) {
        this.connectName = connectName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Connect getConnect() {
        return connect;
    }

    public void setConnect(Connect connect) {
        this.connect = connect;
    }



    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        if (cancelled) {
            return;
        }
        cancelled = true;
        if (future != null) {
            future.cancel(true);
        }
        if (stmt != null) {
            try {
                stmt.cancel();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}

