package com.dbboys.api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SqlexeRepository {

    void setDatabase(Connection conn, String databaseName) throws SQLException;

    List<String> getSqlMode(Connection conn) throws SQLException;

    default void changeSqlMode(Connection conn, String sqlMode) throws SQLException {
        throw new UnsupportedOperationException("SQL mode is not supported");
    }

    default String detectSqlMode(String sql) {
        return null;
    }

    default boolean autoCommitsDdl(String sqlMode) {
        return false;
    }

    default String explain(Connection conn, String sql) throws SQLException {
        throw new UnsupportedOperationException("Explain is not supported");
    }

    default boolean requiresSessionRecovery(SQLException e) {
        return false;
    }

    default void recoverSession(Connection conn, String databaseName) throws SQLException {
        // no-op by default
    }
}
