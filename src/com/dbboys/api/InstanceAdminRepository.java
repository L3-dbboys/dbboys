package com.dbboys.api;

import com.dbboys.customnode.CustomSpaceChart;
import com.dbboys.vo.Connect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 实例级管理能力，如空间信息与扩容策略。
 */
public interface InstanceAdminRepository {

    default boolean supportsAdminFeatures(Connect connect) {
        return false;
    }

    default boolean supportsHealthCheck(Connect connect) {
        return supportsAdminFeatures(connect);
    }

    default boolean supportsOnlineLog(Connect connect) {
        return supportsAdminFeatures(connect);
    }

    default boolean supportsSpaceManager(Connect connect) {
        return supportsAdminFeatures(connect);
    }

    default boolean supportsConfigManagement(Connect connect) {
        return supportsAdminFeatures(connect);
    }

    default boolean supportsStartStop(Connect connect) {
        return supportsAdminFeatures(connect);
    }

    default boolean supportsSpaceMutation(Connect connect) {
        return supportsSpaceManager(connect);
    }

    void setStorageSegmentExtendable(Connection conn, int segmentId, boolean extendable) throws SQLException;

    void resizeStorageSpace(Connection conn, String storageSpaceName, int size1, int size2, int size3) throws SQLException;

    List<List<CustomSpaceChart.SpaceUsage>> getStorageSpaceUsage(Connection conn) throws SQLException;

    double getMaxStorageSpaceUsage(Connection conn) throws SQLException;

    default LockSessionResult getLockSessions(Connection conn, String databaseName, String tableName) throws SQLException {
        throw new UnsupportedOperationException("Lock sessions are not supported");
    }

    default void killLockSession(Connect connect, String owner) throws Exception {
        throw new UnsupportedOperationException("KILL lock session is not supported");
    }

    default boolean canKillLockSession(Connect connect) {
        return false;
    }

    record LockSessionResult(List<String> columns, List<List<String>> rows) {
    }
}
