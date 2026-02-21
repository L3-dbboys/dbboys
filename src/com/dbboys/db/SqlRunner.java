package com.dbboys.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlRunner {
    private final Connection conn;
    private final int timeoutSeconds;

    public SqlRunner(Connection conn, int timeoutSeconds) {
        this.conn = conn;
        this.timeoutSeconds = timeoutSeconds;
    }

    public <T> List<T> query(String sql, List<Object> params, RowMapper<T> mapper) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setQueryTimeout(timeoutSeconds);
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapper.map(rs));
                }
                return list;
            }
        }
    }

    public <T> T queryOne(String sql, List<Object> params, RowMapper<T> mapper) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setQueryTimeout(timeoutSeconds);
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapper.map(rs);
            }
        }
    }

    public int update(String sql, List<Object> params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setQueryTimeout(timeoutSeconds);
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    public boolean execute(String sql, List<Object> params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setQueryTimeout(timeoutSeconds);
            bind(ps, params);
            return ps.execute();
        }
    }

    private void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        if (params == null || params.isEmpty()) {
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
