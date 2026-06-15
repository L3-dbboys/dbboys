package com.dbboys.service;

import com.dbboys.core.DatabasePlatformResolver;
import com.dbboys.core.InstanceAdminRepository;
import com.dbboys.app.AppContext;
import com.dbboys.model.SpaceUsage;
import com.dbboys.core.ConnectionService;
import com.dbboys.model.Connect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AdminService {
    private final ConnectionService connectionService;
    private final DatabasePlatformResolver platformResolver;

    public AdminService() {
        this(resolveConnectionService(), resolvePlatformResolver());
    }

    public AdminService(ConnectionService connectionService, DatabasePlatformResolver platformResolver) {
        this.connectionService = connectionService;
        this.platformResolver = platformResolver;
    }

    public void setStorageSegmentExtendable(Connect connect, int segmentId, boolean extendable) throws Exception {
        Connection conn = connectionService.getConnectionWithSessionInit(connect);
        setStorageSegmentExtendable(platformResolver.admin(connect), conn, segmentId, extendable);
        conn.close();
    }

    public void removeStorageSpaceLimit(Connect connect, String storageSpaceName) throws Exception {
        Connection conn = connectionService.getConnectionWithSessionInit(connect);
        resizeStorageSpace(platformResolver.admin(connect), conn, storageSpaceName, 10, 10000, 0);
        conn.close();
    }

    public List<List<SpaceUsage>> getStorageSpaceUsage(Connect connect) throws Exception {
        Connection conn = connectionService.getConnectionWithSessionInit(connect);
        List<List<SpaceUsage>> result = getStorageSpaceUsage(platformResolver.admin(connect), conn);
        conn.close();
        return result;
    }

    public double getMaxStorageSpaceUsage(Connect connect) throws Exception {
        Connection conn = connectionService.getConnectionWithSessionInit(connect);
        double result = getMaxStorageSpaceUsage(platformResolver.admin(connect), conn);
        conn.close();
        return result;
    }

    public InstanceAdminRepository.LockSessionResult getLockSessions(Connect connect, String databaseName, String tableName) throws Exception {
        try (Connection conn = connectionService.getConnectionWithSessionInit(connect)) {
            return getLockSessions(platformResolver.admin(connect), conn, databaseName, tableName);
        }
    }

    public void killLockSession(Connect connect, String owner) throws Exception {
        platformResolver.admin(connect).killLockSession(connect, owner);
    }

    public boolean canKillLockSession(Connect connect) {
        if (connect == null || connect.getReadonly()) {
            return false;
        }
        return platformResolver.admin(connect).canKillLockSession(connect);
    }

    public String killLockSessionCommand(Connect connect, String owner) {
        return platformResolver.admin(connect).killLockSessionCommand(owner);
    }

    public String getLockSessionDetail(Connect connect, String sid) throws Exception {
        return platformResolver.admin(connect).getLockSessionDetail(connect, sid);
    }

    public boolean canShowLockSessionDetail(Connect connect) {
        return connect != null && platformResolver.admin(connect).canShowLockSessionDetail(connect);
    }

    public String lockSessionDetailCommand(Connect connect, String sid) {
        return platformResolver.admin(connect).lockSessionDetailCommand(sid);
    }

    private void setStorageSegmentExtendable(InstanceAdminRepository adminRepository, Connection conn, int segmentId, boolean extendable) throws SQLException {
        adminRepository.setStorageSegmentExtendable(conn, segmentId, extendable);
    }

    private void resizeStorageSpace(InstanceAdminRepository adminRepository, Connection conn, String storageSpaceName, int size1, int size2, int size3) throws SQLException {
        adminRepository.resizeStorageSpace(conn, storageSpaceName, size1, size2, size3);
    }

    private List<List<SpaceUsage>> getStorageSpaceUsage(InstanceAdminRepository adminRepository, Connection conn) throws SQLException {
        return adminRepository.getStorageSpaceUsage(conn);
    }

    private double getMaxStorageSpaceUsage(InstanceAdminRepository adminRepository, Connection conn) throws SQLException {
        return adminRepository.getMaxStorageSpaceUsage(conn);
    }

    private InstanceAdminRepository.LockSessionResult getLockSessions(InstanceAdminRepository adminRepository,
                                                                      Connection conn,
                                                                      String databaseName,
                                                                      String tableName) throws SQLException {
        return adminRepository.getLockSessions(conn, databaseName, tableName);
    }

    private static ConnectionService resolveConnectionService() {
        try {
            return AppContext.get(ConnectionService.class);
        } catch (IllegalStateException e) {
            return new com.dbboys.core.ConnectionServiceImpl();
        }
    }

    private static DatabasePlatformResolver resolvePlatformResolver() {
        return DatabasePlatformResolver.getInstance();
    }
}
