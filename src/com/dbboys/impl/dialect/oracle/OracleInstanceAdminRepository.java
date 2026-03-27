package com.dbboys.impl.dialect.oracle;

import com.dbboys.api.InstanceAdminRepository;
import com.dbboys.customnode.CustomSpaceChart;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Oracle 实例管理占位实现。
 */
public final class OracleInstanceAdminRepository implements InstanceAdminRepository {

    private static final String MSG = "Oracle instance admin repository not implemented";

    @Override
    public void modifyChunkExtendable(Connection conn, int chunkId, boolean toExtendable) throws SQLException {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public void modifySpaceSize(Connection conn, String dbspace, int size1, int size2, int size3) throws SQLException {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public List<List<CustomSpaceChart.SpaceUsage>> getInstanceDbspaceInfo(Connection conn) throws SQLException {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public double getMaxDbspaceUsed(Connection conn) throws SQLException {
        throw new UnsupportedOperationException(MSG);
    }
}
