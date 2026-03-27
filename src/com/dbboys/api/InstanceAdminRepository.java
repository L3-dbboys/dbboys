package com.dbboys.api;

import com.dbboys.customnode.CustomSpaceChart;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 实例级管理能力，如空间信息与扩容策略。
 */
public interface InstanceAdminRepository {

    void modifyChunkExtendable(Connection conn, int chunkId, boolean toExtendable) throws SQLException;

    void modifySpaceSize(Connection conn, String dbspace, int size1, int size2, int size3) throws SQLException;

    List<List<CustomSpaceChart.SpaceUsage>> getInstanceDbspaceInfo(Connection conn) throws SQLException;

    double getMaxDbspaceUsed(Connection conn) throws SQLException;
}
