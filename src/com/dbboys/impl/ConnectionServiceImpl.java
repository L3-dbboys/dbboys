package com.dbboys.impl;

import com.dbboys.api.ConnectionService;
import com.dbboys.api.MetadataRepositoryProvider;
import com.dbboys.api.DatabaseDialect;
import com.dbboys.impl.dialect.DatabaseDialectRegistry;
import com.dbboys.i18n.I18n;
import com.dbboys.util.MD5Util;
import com.dbboys.db.local.LocalDbRepository;
import com.dbboys.vo.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConnectionServiceImpl implements ConnectionService {
    private static final Logger log = LogManager.getLogger(ConnectionServiceImpl.class);
    private static final Map<String, Driver> DRIVER_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, URLClassLoader> LOADER_CACHE = new ConcurrentHashMap<>();
    private final MetadataRepositoryProvider metadataRepositoryProvider;
    private final DatabaseDialectRegistry dialectRegistry;

    public ConnectionServiceImpl() {
        this(new DefaultMetadataRepositoryProvider(DatabaseDialectRegistry.createDefault()), DatabaseDialectRegistry.createDefault());
    }

    public ConnectionServiceImpl(MetadataRepositoryProvider metadataRepositoryProvider) {
        this(metadataRepositoryProvider, DatabaseDialectRegistry.createDefault());
    }

    public ConnectionServiceImpl(MetadataRepositoryProvider metadataRepositoryProvider, DatabaseDialectRegistry dialectRegistry) {
        this.metadataRepositoryProvider = metadataRepositoryProvider;
        this.dialectRegistry = dialectRegistry != null ? dialectRegistry : DatabaseDialectRegistry.createDefault();
    }

    public Connection createConnection(Connect connect) throws Exception {
        DatabaseDialect dialect = dialectRegistry.getDialect(connect.getDbtype());
        if (dialect == null) {
            throw new IllegalArgumentException("Unsupported database type: " + connect.getDbtype());
        }
        DatabaseDialect.ConnectionParams params = dialect.getConnectionParams(connect);
        Driver driver = getOrLoadDriver(params.getDriverClassName(), params.getJarFilePath());
        Properties info = buildConnectionProperties(connect);
        return driver.connect(params.getUrl(), info);
    }

    private static Properties buildConnectionProperties(Connect connect) {
        Properties info = new Properties();
        info.setProperty("user", connect.getUsername());
        info.setProperty("password", connect.getPassword());
        JSONArray jsonArray = new JSONArray(connect.getProps());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString("propValue") != null && (!jsonObject.getString("propValue").equals(""))) {
                info.setProperty(jsonObject.getString("propName"), jsonObject.getString("propValue"));
            }
        }
        return info;
    }

    private Driver getOrLoadDriver(String className, String jarFilePath) throws Exception {
        if (className == null || jarFilePath == null) {
            throw new IllegalArgumentException("className/jarFilePath is null");
        }
        String key = className + "|" + jarFilePath;
        Driver cached = DRIVER_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        synchronized (DRIVER_CACHE) {
            cached = DRIVER_CACHE.get(key);
            if (cached != null) {
                return cached;
            }
            URLClassLoader loader = LOADER_CACHE.get(jarFilePath);
            if (loader == null) {
                loader = new URLClassLoader(new URL[]{new URL(jarFilePath)}, ClassLoader.getPlatformClassLoader());
                LOADER_CACHE.put(jarFilePath, loader);
            }
            Driver driver = (Driver) Class.forName(className, true, loader).getDeclaredConstructor().newInstance();
            DRIVER_CACHE.put(key, driver);
            return driver;
        }
    }

    public Connection getGbaseModeConnection(Connect connect) throws Exception {
        Connection conn = createConnection(connect);
        initializeSessionIfSupported(connect, conn);
        return conn;
    }

    public Connection getConnection(Connect connect) throws Exception {
        return createConnection(connect);
    }

    public void changeCommitMode(Connection conn, int commitChoiceBoxIndex) throws SQLException {
        if (commitChoiceBoxIndex == 0) {
            conn.setAutoCommit(true);
        } else if (commitChoiceBoxIndex == 1) {
            conn.setAutoCommit(false);
        }
    }

    private void initializeSessionIfSupported(Connect connect, Connection conn) {
        if (connect == null || conn == null) {
            return;
        }
        DatabaseDialect dialect = dialectRegistry.getDialect(connect.getDbtype());
        if (dialect != null && dialect.supportsSessionInit()) {
            try {
                dialect.sessionInit(conn, connect);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public ChangeDefaultDatabaseResult changeDefaultDatabase(Connect connect, Database database) {
        ChangeDefaultDatabaseResult result = new ChangeDefaultDatabaseResult();
        if (connect == null || database == null) {
            result.setSuccess(false);
            return result;
        }
        try {
            metadataRepositoryProvider.get(connect).changeDatabase(connect.getConn(), database.getName());
            connect.setDatabase(database.getName());
            if(database.getName().equals("sysmaster")||database.getName().equals("sysadmin")||database.getName().equals("sysutils")||database.getName().equals("syscdcv1")||database.getName().equals("sys")||database.getName().equals("gbasedbt")){
            }else{
                connect.setProps(modifyProps(connect, database.getDbLocale()));

            }
            connect.setProps(modifyProps(connect, database.getDbLocale()));
            LocalDbRepository.updateConnect(connect);
            result.setSuccess(true);
        } catch (SQLException e) {
            if (e.getErrorCode() == -79716 || e.getErrorCode() == -79730) {
                result.setDisconnected(true);
            } else if (e.getErrorCode() == -23197 || e.getErrorCode() == -349) {
                try {
                    connect.getConn().close();
                    connect.setDatabase(database.getName());
                    connect.setProps(modifyProps(connect, database.getDbLocale()));
                    connect.setConn(getConnection(connect));
                    initializeSessionIfSupported(connect, connect.getConn());
                    LocalDbRepository.updateConnect(connect);
                    result.setSuccess(true);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                result.setErrorCode(e.getErrorCode());
                result.setErrorMessage(e.getMessage());
            }
        }
        return result;
    }

    public String modifyProps(Connect connect, String DBlocale) {
        if (connect == null) {
            return null;
        }
        DatabaseDialect dialect = dialectRegistry.getDialect(connect.getDbtype());
        if (dialect == null) {
            return connect.getProps();
        }
        return dialect.adjustProps(connect, DBlocale);
    }

    public String setConnectInfo(Connect connect) throws Exception {
        String primaryInstance = "";
        Connection connection = getGbaseModeConnection(connect);

        ResultSet rs = null;
        String dbversion = null;
        if (connect.getUsername().equals("gbasedbt")) {
            rs = connection.createStatement().executeQuery("EXECUTE FUNCTION sysadmin:task('onstat','-V');");
            rs.next();
            dbversion = rs.getString(1).replace("GBase Database Server Version 12.10.FC4G1", "")
                    .replace(" Software Serial Number AAA#B000000", "")
                    .replace("\n", "");
            if (!dbversion.contains("GBase8s")) {
                DatabaseMetaData metaData = connection.getMetaData();
                String databaseProductVersion = metaData.getDatabaseProductVersion();
                dbversion = "GBase8sV" + databaseProductVersion + "_" + dbversion;
            }
        } else {
            dbversion = I18n.t("metadata.dbversion.no_permission",
                    "当前用户无权限获取版本信息，请使用gbasedbt用户连接获取\n");
        }
        connect.setDbversion(dbversion);
        String info = "##########################################################################################\n";
        info += "Instance Boot Information\n";
        info += "##########################################################################################\n";
        rs = connection.createStatement().executeQuery("select env_name,trim(env_value) from sysmaster:sysenv");

        while (rs.next()) {
            info += String.format("%-30s", rs.getString(1)) + rs.getString(2) + "\n";
            if (rs.getString(1).equals("DB_LOCALE")) {
                connect.setProps(connect.getProps().replace("{\"propValue\":\"\",\"propName\":\"DB_LOCALE\"}",
                        "{\"propValue\":\"" + rs.getString(2).toUpperCase().trim()
                                .replace("ZH_CN.GB18030-2000", "zh_CN.5488")
                                .replace("ZH_CN.UTF8", "zh_CN.57372")
                                + "\",\"propName\":\"DB_LOCALE\"}"));
                connect.setProps(connect.getProps().replace("{\"propName\":\"DB_LOCALE\",\"propValue\":\"\"}",
                        "{\"propName\":\"DB_LOCALE\",\"propValue\":\"" + rs.getString(2).toUpperCase().trim()
                                .replace("ZH_CN.GB18030-2000", "zh_CN.5488")
                                .replace("ZH_CN.UTF8", "zh_CN.57372")
                                + "\"}"));
            }
        }
        rs.close();
        info += "\n##########################################################################################\n";
        info += "System Information\n";
        info += "##########################################################################################\n";
        rs = connection.createStatement().executeQuery("SELECT * from sysmaster:sysmachineinfo ");
        rs.next();
        for (int i = 1; i <= 24; i++) {
            info += String.format("%-30s", rs.getMetaData().getColumnName(i));
            info += rs.getString(i) + "\n";
        }
        rs.close();

        if (!connect.getPropByName("GBASEDBTSERVER").isEmpty()) {
            rs = connection.createStatement().executeQuery("select dbservername from dual");
            if (rs.next()) {
                primaryInstance = rs.getString(1);
            }
        }
        rs.close();
        connection.close();
        connect.setInfo(info);

        connect.setDrivermd5(MD5Util.getMD5Checksum(Paths.get("extlib/" + connect.getDbtype() + "/" + connect.getDriver()).toFile().getAbsolutePath()));
        return primaryInstance;
    }

    public Boolean testConn(Connect connect) {
        Boolean result = false;
        ResultSet rs = null;
        if (connect.getConn() != null) {
            try {
                rs = connect.getConn().createStatement().executeQuery("select first 1 tabid from systables");
                result = true;
            } catch (SQLException e) {
                log.error("Operation failed", e);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        rs = null;
                    }
                }
            }
        }
        return result;
    }

    @FunctionalInterface
    private interface SqlFunction<T, R> {
        R apply(T value) throws Exception;
    }

    private static class ConnectionLease {
        private final Connection conn;
        private final boolean shouldClose;

        private ConnectionLease(Connection conn, boolean shouldClose) {
            this.conn = conn;
            this.shouldClose = shouldClose;
        }
    }

    private ConnectionLease acquireConnection(Connect connect, Database database) throws Exception {
        Connection conn = connect.getConn();
        var repo = metadataRepositoryProvider.get(connect);
        try {
            repo.setDatabase(conn, database.getName());
            return new ConnectionLease(conn, false);
        } catch (SQLException e) {
            DatabaseDialect dialect = dialectRegistry.getDialect(connect.getDbtype());
            if (dialect != null && dialect.supportsSessionInit() && e.getErrorCode() == -23197) {
                repo.setDatabase(connect.getConn(), "sysmaster");
                Connect connect1 = new Connect(connect);
                connect1.setDatabase(database.getName());
                connect1.setProps(modifyProps(connect1, database.getDbLocale()));
                Connection newConn = getConnection(connect1);
                initializeSessionIfSupported(connect1, newConn);
                repo.setDatabase(newConn, database.getName());
                return new ConnectionLease(newConn, true);
            }
            throw e;
        }
    }

    public <T> T withMetaSession(Connect connect, Database database, SqlWork<T> action) throws Exception {
        if (connect == null) {
            return null;
        }
        if (database == null) {
            return action.apply(connect.getConn());
        }
        ConnectionLease lease = acquireConnection(connect, database);
        try {
            return action.apply(lease.conn);
        } finally {
            if (lease.shouldClose) {
                lease.conn.close();
            }
        }
    }
}
