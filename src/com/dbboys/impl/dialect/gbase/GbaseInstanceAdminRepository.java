package com.dbboys.impl.dialect.gbase;

import com.dbboys.api.InstanceAdminRepository;
import com.dbboys.customnode.CustomSpaceChart;
import com.dbboys.vo.Connect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * GBase 实例管理实现。
 */
public final class GbaseInstanceAdminRepository implements InstanceAdminRepository {

    private final GbaseAdminSupport delegate = new GbaseAdminSupport();

    @Override
    public boolean supportsAdminFeatures(Connect connect) {
        return connect != null && "gbasedbt".equalsIgnoreCase(connect.getUsername());
    }

    @Override
    public void modifyChunkExtendable(Connection conn, int chunkId, boolean toExtendable) throws SQLException {
        delegate.modifyChunkExtendable(conn, chunkId, toExtendable);
    }

    @Override
    public void modifySpaceSize(Connection conn, String dbspace, int size1, int size2, int size3) throws SQLException {
        delegate.modifySpaceSize(conn, dbspace, size1, size2, size3);
    }

    @Override
    public List<List<CustomSpaceChart.SpaceUsage>> getInstanceDbspaceInfo(Connection conn) throws SQLException {
        return delegate.getInstanceDbspaceInfo(conn);
    }

    @Override
    public double getMaxDbspaceUsed(Connection conn) throws SQLException {
        return delegate.getMaxDbspaceUsed(conn);
    }
}
