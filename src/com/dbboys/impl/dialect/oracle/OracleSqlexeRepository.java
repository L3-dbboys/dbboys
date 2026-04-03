package com.dbboys.impl.dialect.oracle;

import com.dbboys.api.SqlexeRepository;

import java.sql.Connection;
import java.sql.SQLException;

public final class OracleSqlexeRepository implements SqlexeRepository {

    @Override
    public void setDatabase(Connection conn, String databaseName) throws SQLException {
        if (conn == null || databaseName == null || databaseName.isBlank()) {
            return;
        }
        String quotedSchema = "\"" + databaseName.replace("\"", "\"\"") + "\"";
        try (var stmt = conn.createStatement()) {
            stmt.execute("alter session set current_schema = " + quotedSchema);
        }
    }

    @Override
    public boolean autoCommitsDdl() {
        return true;
    }
}
