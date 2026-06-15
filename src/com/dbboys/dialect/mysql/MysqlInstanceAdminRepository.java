package com.dbboys.dialect.mysql;

import com.dbboys.core.InstanceAdminRepository;
import com.dbboys.core.ConnectionServiceImpl;
import com.dbboys.model.SpaceUsage;
import com.dbboys.model.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class MysqlInstanceAdminRepository implements InstanceAdminRepository {
    private static final String SQL_LOCK_SESSIONS_80 = """
            select distinct
                   cast(t.processlist_id as char) as owner,
                   dl.object_schema as dbsname,
                   dl.object_name as tabname,
                   dl.lock_type,
                   dl.lock_mode,
                   dl.lock_status,
                   t.processlist_user as username,
                   t.processlist_host as host,
                   t.processlist_info as sql_text
            from performance_schema.data_locks dl
            left join performance_schema.threads t
              on t.thread_id = dl.thread_id
            where dl.object_schema = ?
              and dl.object_name = ?
            order by owner
            """;
    private static final String SQL_LOCK_SESSIONS_57 = """
            select distinct
                   cast(t.trx_mysql_thread_id as char) as owner,
                   il.lock_table as table_name,
                   il.lock_type,
                   il.lock_mode,
                   il.lock_data,
                   p.user as username,
                   p.host,
                   p.info as sql_text
            from information_schema.innodb_locks il
            left join information_schema.innodb_trx t
              on t.trx_id = il.lock_trx_id
            left join information_schema.processlist p
              on p.id = t.trx_mysql_thread_id
            where il.lock_table = concat('`', ?, '`.`', ?, '`')
            order by owner
            """;
    private static final String SQL_SESSION_DETAIL_PERFORMANCE_SCHEMA = """
            select t.processlist_id as id,
                   t.processlist_user as user,
                   t.processlist_host as host,
                   t.processlist_db as db,
                   t.processlist_command as command,
                   t.processlist_time as time,
                   t.processlist_state as state,
                   coalesce(t.processlist_info, esc.sql_text, esh.sql_text) as sql_text,
                   esc.sql_text as current_sql,
                   esh.sql_text as last_sql
            from performance_schema.threads t
            left join performance_schema.events_statements_current esc
              on esc.thread_id = t.thread_id
            left join performance_schema.events_statements_history esh
              on esh.thread_id = t.thread_id
            where t.processlist_id = ?
            order by esh.event_id desc
            limit 1
            """;
    @Override
    public boolean supportsAdminFeatures(com.dbboys.model.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsHealthCheck(com.dbboys.model.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsOnlineLog(com.dbboys.model.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsSpaceManager(com.dbboys.model.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsConfigManagement(com.dbboys.model.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsStartStop(com.dbboys.model.Connect connect) {
        return false;
    }

    @Override
    public boolean supportsSpaceMutation(com.dbboys.model.Connect connect) {
        return false;
    }

    @Override
    public void setStorageSegmentExtendable(Connection conn, int segmentId, boolean extendable) {
        throw new UnsupportedOperationException("MySQL does not support storage segment extend operations");
    }

    @Override
    public void resizeStorageSpace(Connection conn, String storageSpaceName, int size1, int size2, int size3) {
        throw new UnsupportedOperationException("MySQL does not support storage space resize operations");
    }

    @Override
    public List<List<SpaceUsage>> getStorageSpaceUsage(Connection conn) throws SQLException {
        List<SpaceUsage> engineUsage = new ArrayList<>();
        String sql = """
                select engine_name, data_bytes, index_bytes, table_count
                from (
                    select coalesce(engine, 'UNKNOWN') as engine_name,
                           coalesce(sum(data_length), 0) as data_bytes,
                           coalesce(sum(index_length), 0) as index_bytes,
                           count(*) as table_count
                    from information_schema.tables
                    where table_schema not in ('information_schema', 'mysql', 'performance_schema', 'sys')
                    group by coalesce(engine, 'UNKNOWN')
                ) t
                order by data_bytes + index_bytes desc
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int no = 1;
            while (rs.next()) {
                double dataGb = bytesToGb(rs.getLong("data_bytes"));
                double indexGb = bytesToGb(rs.getLong("index_bytes"));
                double total = dataGb + indexGb;
                engineUsage.add(new SpaceUsage(
                        no++,
                        rs.getString("engine_name"),
                        rs.getString("engine_name"),
                        0,
                        total,
                        dataGb,
                        rs.getInt("table_count"),
                        0,
                        0,
                        indexGb,
                        indexGb));
            }
        }

        List<SpaceUsage> databaseUsage = new ArrayList<>();
        sql = """
                select table_schema, data_bytes, index_bytes, table_count
                from (
                    select table_schema,
                           coalesce(sum(data_length), 0) as data_bytes,
                           coalesce(sum(index_length), 0) as index_bytes,
                           count(*) as table_count
                    from information_schema.tables
                    where table_schema not in ('information_schema', 'mysql', 'performance_schema', 'sys')
                    group by table_schema
                ) t
                order by data_bytes + index_bytes desc
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int no = 1;
            while (rs.next()) {
                double dataGb = bytesToGb(rs.getLong("data_bytes"));
                double indexGb = bytesToGb(rs.getLong("index_bytes"));
                String name = rs.getString("table_schema");
                double used = dataGb + indexGb;
                databaseUsage.add(new SpaceUsage(
                        no++, name, name, 0, used, used, rs.getInt("table_count"), 0, 0, 0, 0));
            }
        }

        List<SpaceUsage> dataIndexUsage = new ArrayList<>();
        for (SpaceUsage db : databaseUsage) {
            dataIndexUsage.add(db);
        }

        List<SpaceUsage> tableUsage = new ArrayList<>();
        sql = """
                select table_schema, table_name, engine,
                       coalesce(data_length, 0) as data_bytes,
                       coalesce(index_length, 0) as index_bytes,
                       coalesce(table_rows, 0) as table_rows
                from information_schema.tables
                where table_schema not in ('information_schema', 'mysql', 'performance_schema', 'sys')
                  and table_type = 'BASE TABLE'
                order by data_length + index_length desc
                limit 20
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int no = 1;
            while (rs.next()) {
                double dataGb = bytesToGb(rs.getLong("data_bytes"));
                double indexGb = bytesToGb(rs.getLong("index_bytes"));
                String label = rs.getString("table_schema") + "." + rs.getString("table_name")
                        + " [" + rs.getString("engine") + "]";
                tableUsage.add(new SpaceUsage(
                        no++,
                        label,
                        rs.getString("table_schema") + "." + rs.getString("table_name"),
                        0,
                        dataGb + indexGb,
                        dataGb + indexGb,
                        0,
                        Math.max(0, rs.getInt("table_rows")),
                        Math.max(0, rs.getInt("table_rows")),
                        0,
                        0));
            }
        }
        return List.of(engineUsage, dataIndexUsage, databaseUsage, tableUsage);
    }

    @Override
    public double getMaxStorageSpaceUsage(Connection conn) throws SQLException {
        double max = 0;
        for (SpaceUsage usage : getStorageSpaceUsage(conn).get(0)) {
            max = Math.max(max, usage.getUsed());
        }
        return max;
    }

    @Override
    public boolean supportsLockSession(com.dbboys.model.Connect connect) {
        return true;
    }

    @Override
    public LockSessionResult getLockSessions(Connection conn, String databaseName, String tableName) throws SQLException {
        try {
            return getLockSessions(conn, SQL_LOCK_SESSIONS_80, databaseName, tableName);
        } catch (SQLException first) {
            try {
                return getLockSessions(conn, SQL_LOCK_SESSIONS_57, databaseName, tableName);
            } catch (SQLException ignored) {
                throw first;
            }
        }
    }

    @Override
    public void killLockSession(Connect connect, String owner) throws Exception {
        validateOwner(owner);
        try (Connection conn = new ConnectionServiceImpl().getConnectionWithSessionInit(connect);
             Statement stmt = conn.createStatement()) {
            stmt.execute(killLockSessionCommand(owner));
        }
    }

    @Override
    public boolean canKillLockSession(Connect connect) {
        return connect != null;
    }

    @Override
    public String killLockSessionCommand(String owner) {
        validateOwner(owner);
        return "KILL CONNECTION " + owner;
    }

    @Override
    public String getLockSessionDetail(Connect connect, String sid) throws Exception {
        validateOwner(sid);
        try {
            return getPerformanceSchemaSessionDetail(connect, sid);
        } catch (SQLException ignored) {
            // MySQL 5.7 or restricted accounts may not expose performance_schema statement tables.
        }
        try (Connection conn = new ConnectionServiceImpl().getConnectionWithSessionInit(connect);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW FULL PROCESSLIST")) {
            long targetId = Long.parseLong(sid);
            ResultSetMetaData metaData = rs.getMetaData();
            int idColumn = findColumnIndex(metaData, "Id");
            while (rs.next()) {
                if (rs.getLong(idColumn) == targetId) {
                    return formatResult(toSingleRowLockSessionResult(rs, metaData));
                }
            }
            return "";
        }
    }

    @Override
    public boolean canShowLockSessionDetail(Connect connect) {
        return connect != null;
    }

    @Override
    public String lockSessionDetailCommand(String sid) {
        validateOwner(sid);
        return "SHOW FULL PROCESSLIST -- Id = " + sid;
    }

    private String getPerformanceSchemaSessionDetail(Connect connect, String sid) throws Exception {
        try (Connection conn = new ConnectionServiceImpl().getConnectionWithSessionInit(connect);
             PreparedStatement pstmt = conn.prepareStatement(SQL_SESSION_DETAIL_PERFORMANCE_SCHEMA)) {
            pstmt.setLong(1, Long.parseLong(sid));
            try (ResultSet rs = pstmt.executeQuery()) {
                return formatResult(toLockSessionResult(rs));
            }
        }
    }

    private static double bytesToGb(long bytes) {
        return Math.round(bytes / 1024d / 1024d / 1024d * 100d) / 100d;
    }

    private static int findColumnIndex(ResultSetMetaData metaData, String columnName) throws SQLException {
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String label = metaData.getColumnLabel(i);
            if (columnName.equalsIgnoreCase(label)) {
                return i;
            }
        }
        return 1;
    }

    private static LockSessionResult toSingleRowLockSessionResult(ResultSet resultSet,
                                                                  ResultSetMetaData metaData) throws SQLException {
        int columnCount = metaData.getColumnCount();
        List<String> columns = new ArrayList<>();
        List<String> row = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            String name = metaData.getColumnLabel(i);
            if (name == null || name.isBlank()) {
                name = metaData.getColumnName(i);
            }
            columns.add(name == null || name.isBlank() ? "COL" + i : name);
            Object value = resultSet.getObject(i);
            row.add(value == null || resultSet.wasNull() ? null : String.valueOf(value));
        }
        return new LockSessionResult(columns, List.of(row));
    }

    private static LockSessionResult getLockSessions(Connection conn,
                                                     String sql,
                                                     String databaseName,
                                                     String tableName) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, databaseName == null ? "" : databaseName.trim());
            pstmt.setString(2, tableName == null ? "" : tableName.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                return toLockSessionResult(rs);
            }
        }
    }

    private static LockSessionResult toLockSessionResult(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            String name = metaData.getColumnLabel(i);
            if (name == null || name.isBlank()) {
                name = metaData.getColumnName(i);
            }
            columns.add(name == null || name.isBlank() ? "COL" + i : name);
        }
        List<List<String>> rows = new ArrayList<>();
        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                Object value = resultSet.getObject(i);
                row.add(value == null || resultSet.wasNull() ? null : String.valueOf(value));
            }
            rows.add(row);
        }
        return new LockSessionResult(columns, rows);
    }

    private static void validateOwner(String owner) {
        if (owner == null || !owner.trim().matches("\\d+")) {
            throw new IllegalArgumentException("Invalid MySQL session id: " + owner);
        }
    }

    private static String formatResult(LockSessionResult result) {
        if (result.rows().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        List<String> columns = result.columns();
        for (List<String> row : result.rows()) {
            for (int i = 0; i < columns.size(); i++) {
                sb.append(columns.get(i)).append(": ");
                if (i < row.size() && row.get(i) != null) {
                    sb.append(row.get(i));
                }
                sb.append('\n');
            }
        }
        return sb.toString().trim();
    }
}
