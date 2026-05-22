package com.dbboys.impl.dialect.mysql;

import com.dbboys.api.InstanceAdminRepository;
import com.dbboys.customnode.CustomSpaceChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MysqlInstanceAdminRepository implements InstanceAdminRepository {

    @Override
    public boolean supportsAdminFeatures(com.dbboys.vo.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsHealthCheck(com.dbboys.vo.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsOnlineLog(com.dbboys.vo.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsSpaceManager(com.dbboys.vo.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsConfigManagement(com.dbboys.vo.Connect connect) {
        return true;
    }

    @Override
    public boolean supportsStartStop(com.dbboys.vo.Connect connect) {
        return false;
    }

    @Override
    public boolean supportsSpaceMutation(com.dbboys.vo.Connect connect) {
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
    public List<List<CustomSpaceChart.SpaceUsage>> getStorageSpaceUsage(Connection conn) throws SQLException {
        List<CustomSpaceChart.SpaceUsage> engineUsage = new ArrayList<>();
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
                engineUsage.add(new CustomSpaceChart.SpaceUsage(
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

        List<CustomSpaceChart.SpaceUsage> databaseUsage = new ArrayList<>();
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
                databaseUsage.add(new CustomSpaceChart.SpaceUsage(
                        no++, name, name, 0, used, used, rs.getInt("table_count"), 0, 0, 0, 0));
            }
        }

        List<CustomSpaceChart.SpaceUsage> dataIndexUsage = new ArrayList<>();
        for (CustomSpaceChart.SpaceUsage db : databaseUsage) {
            dataIndexUsage.add(db);
        }

        List<CustomSpaceChart.SpaceUsage> tableUsage = new ArrayList<>();
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
                tableUsage.add(new CustomSpaceChart.SpaceUsage(
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
        for (CustomSpaceChart.SpaceUsage usage : getStorageSpaceUsage(conn).get(0)) {
            max = Math.max(max, usage.getUsed());
        }
        return max;
    }

    private static double bytesToGb(long bytes) {
        return Math.round(bytes / 1024d / 1024d / 1024d * 100d) / 100d;
    }
}
