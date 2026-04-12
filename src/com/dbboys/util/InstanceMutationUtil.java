package com.dbboys.util;

import com.dbboys.api.ConnectionService;
import com.dbboys.api.InstanceTabCapability;
import com.dbboys.app.AppContext;
import com.dbboys.vo.Connect;
import com.jcraft.jsch.Session;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public final class InstanceMutationUtil {
    private InstanceMutationUtil() {
    }

    public static InstanceTabCapability.ConfigUpdateResult updateInformixStyleConfig(Connect connect,
                                                                                     String installDirEnvName,
                                                                                     String paramName,
                                                                                     String newValue) throws Exception {
        String installDirEnv = "$" + installDirEnvName;
        String cmd;
        if ("BUFFERPOOL".equals(paramName) || "VPCLASS".equals(paramName)) {
            cmd = "sed -i \"s#^" + paramName + " *" + newValue.split(",")[0] + ".*#" + paramName + " "
                    + newValue.replace("$", "\\$") + "#g\" " + installDirEnv + "/etc/$ONCONFIG";
        } else {
            cmd = "onmode -wf " + paramName + "=\"" + newValue + "\";sed -i \"s#^" + paramName + ".*#" + paramName
                    + " " + newValue.replace("$", "\\$") + "#g\" " + installDirEnv + "/etc/$ONCONFIG";
        }

        Session session = JschUtil.getConnect(connect);
        try {
            String result = JschUtil.executeCommand(session, JschUtil.extractEnvValue(connect.getInfo()) + cmd, true);
            if (result.contains("has been changed to")) {
                return new InstanceTabCapability.ConfigUpdateResult(
                        InstanceTabCapability.ConfigUpdateStatus.APPLIED,
                        "参数已修改生效！"
                );
            }
            if (result.contains("shared memory not initialized")) {
                return new InstanceTabCapability.ConfigUpdateResult(
                        InstanceTabCapability.ConfigUpdateStatus.FILE_ONLY,
                        "配置文件已修改，数据库未启动，下次启动后生效！"
                );
            }
            return new InstanceTabCapability.ConfigUpdateResult(
                    InstanceTabCapability.ConfigUpdateStatus.RESTART_REQUIRED,
                    "参数已修改，请重启数据库生效！"
            );
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static void startInformixStyleInstance(Connect connect) throws Exception {
        Session session = JschUtil.getConnect(connect);
        try {
            int result = JschUtil.executeCommandWithExitStatus(session, JschUtil.extractEnvValue(connect.getInfo()) + "oninit");
            if (result != 0) {
                throw new Exception("启动数据库失败，请检查日志错误！");
            }
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static void stopInformixStyleInstance(Connect connect) throws Exception {
        Session session = JschUtil.getConnect(connect);
        try {
            int result = JschUtil.executeCommandWithExitStatus(session, JschUtil.extractEnvValue(connect.getInfo()) + "onmode -ky&&onclean -ky");
            if (result != 0) {
                throw new Exception("关闭数据库失败，请检查日志错误！");
            }
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static void createOrAddInformixStyleSpace(Connect connect,
                                                     InstanceTabCapability.SpaceMutationRequest request) throws Exception {
        String cmd;
        if (request.addFile()) {
            cmd = "onspaces -a " + request.spaceName() + " -p " + request.filePath() + " -o 0 -s " + request.sizeKb();
        } else {
            cmd = switch (request.spaceType()) {
                case STANDARD -> "onspaces -c -d " + request.spaceName() + " -p " + request.filePath()
                        + " -o 0 -s " + request.sizeKb() + " -k " + request.pageSizeKb();
                case TEMP -> "onspaces -c -d " + request.spaceName() + " -p " + request.filePath()
                        + " -o 0 -s " + request.sizeKb() + " -k " + request.pageSizeKb() + " -t";
                case BLOB -> "onspaces -c -S " + request.spaceName() + " -p " + request.filePath()
                        + " -o 0 -s " + request.sizeKb() + " -Df \"LOGGING = ON, AVG_LO_SIZE=1\"";
            };
        }
        cmd = "touch " + request.filePath()
                + "&&chown " + request.adminOsUser() + ":" + request.adminOsUser() + " " + request.filePath()
                + "&&chmod 660 " + request.filePath()
                + "&&" + cmd;
        Session session = JschUtil.getConnect(connect);
        try {
            String result = JschUtil.executeCommand(session, JschUtil.extractEnvValue(connect.getInfo()) + cmd);
            if (!(result.contains("Space successfully added") || result.contains("Chunk successfully added"))) {
                throw new Exception(result);
            }
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static void abortCreateOrAddInformixStyleSpace(Connect connect) throws Exception {
        Session session = JschUtil.getConnect(connect);
        try {
            int result = JschUtil.executeCommandWithExitStatus(session, "ps -ef |grep onspaces|grep -v grep |awk '{print \"kill -9 \"$2}' |sh ");
            if (result != 0) {
                throw new Exception("停止创建空间失败！");
            }
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static void dropInformixStyleSpace(Connect connect,
                                              String spaceName,
                                              List<String> datafilePaths) throws Exception {
        StringBuilder cmd = new StringBuilder("onspaces -d ").append(spaceName).append(" -y ");
        if (datafilePaths != null) {
            for (String path : datafilePaths) {
                cmd.append("&& rm -rf ").append(path);
            }
        }
        Session session = JschUtil.getConnect(connect);
        try {
            String result = JschUtil.executeCommand(session, JschUtil.extractEnvValue(connect.getInfo()) + cmd);
            if (!result.contains("Space successfully dropped")) {
                throw new Exception(result);
            }
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static void dropInformixStyleDatafile(Connect connect,
                                                 String spaceName,
                                                 String datafilePath) throws Exception {
        String cmd = "onspaces -d " + spaceName + " -p " + datafilePath + " -o 0 -y&& rm -rf " + datafilePath;
        Session session = JschUtil.getConnect(connect);
        try {
            String result = JschUtil.executeCommand(session, JschUtil.extractEnvValue(connect.getInfo()) + cmd);
            if (!result.contains("Chunk successfully dropped")) {
                throw new Exception(result);
            }
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static InstanceTabCapability.ConfigUpdateResult updateOracleConfig(Connect connect,
                                                                              String paramName,
                                                                              String newValue) throws Exception {
        ConnectionService connectionService = AppContext.get(ConnectionService.class);
        String sql = "alter system set " + paramName + " = " + toOracleAlterSystemLiteral(newValue) + " scope=both sid='*'";
        try (Connection conn = connectionService.getConnectionWithSessionInit(connect);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            return new InstanceTabCapability.ConfigUpdateResult(
                    InstanceTabCapability.ConfigUpdateStatus.APPLIED,
                    "参数已修改生效"
            );
        }
    }

    public static void startOracleInstance(Connect connect) throws Exception {
        Connect prelimConnect = cloneConnectWithOracleProp(connect, "prelim_auth", "true");
        prelimConnect = cloneConnectWithOracleProp(prelimConnect, "internal_logon", "sysdba");
        try (Connection prelimConn = AppContext.get(ConnectionService.class).createConnection(prelimConnect)) {
            Object oracleConnection = unwrapOracleConnection(prelimConn);
            Class<?> startupModeClass = loadOracleClass(oracleConnection, "oracle.jdbc.OracleConnection$DatabaseStartupMode");
            Object noRestriction = Enum.valueOf((Class<Enum>) startupModeClass.asSubclass(Enum.class), "NO_RESTRICTION");
            Method startup = oracleConnection.getClass().getMethod("startup", startupModeClass);
            startup.invoke(oracleConnection, noRestriction);
        }

        Connect sysdbaConnect = cloneConnectWithOracleProp(connect, "internal_logon", "sysdba");
        try (Connection conn = AppContext.get(ConnectionService.class).createConnection(sysdbaConnect);
             Statement stmt = conn.createStatement()) {
            stmt.execute("alter database mount");
            stmt.execute("alter database open");
        }
    }

    public static void stopOracleInstance(Connect connect) throws Exception {
        Connect sysdbaConnect = cloneConnectWithOracleProp(connect, "internal_logon", "sysdba");
        try (Connection conn = AppContext.get(ConnectionService.class).createConnection(sysdbaConnect);
             Statement stmt = conn.createStatement()) {
            Object oracleConnection = unwrapOracleConnection(conn);
            Class<?> shutdownModeClass = loadOracleClass(oracleConnection, "oracle.jdbc.OracleConnection$DatabaseShutdownMode");
            Object immediate = Enum.valueOf((Class<Enum>) shutdownModeClass.asSubclass(Enum.class), "IMMEDIATE");
            Object finalMode = Enum.valueOf((Class<Enum>) shutdownModeClass.asSubclass(Enum.class), "FINAL");
            Method shutdown = oracleConnection.getClass().getMethod("shutdown", shutdownModeClass);
            shutdown.invoke(oracleConnection, immediate);
            stmt.execute("alter database close normal");
            stmt.execute("alter database dismount");
            shutdown.invoke(oracleConnection, finalMode);
        }
    }

    private static Connect cloneConnectWithOracleProp(Connect connect, String propName, String propValue) {
        Connect clone = new Connect(connect);
        ConnectionService connectionService = AppContext.get(ConnectionService.class);
        clone.setProps(connectionService.modifyProps(clone, propName, propValue));
        return clone;
    }

    private static Object unwrapOracleConnection(Connection conn) throws Exception {
        Class<?> oracleConnectionClass = Class.forName("oracle.jdbc.OracleConnection", true, conn.getClass().getClassLoader());
        if (oracleConnectionClass.isInstance(conn)) {
            return conn;
        }
        return conn.unwrap((Class<?>) oracleConnectionClass);
    }

    private static Class<?> loadOracleClass(Object oracleConnection, String className) throws ClassNotFoundException {
        return Class.forName(className, true, oracleConnection.getClass().getClassLoader());
    }

    private static String toOracleAlterSystemLiteral(String value) {
        if (value == null) {
            return "''";
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "''";
        }
        if (trimmed.startsWith("'") && trimmed.endsWith("'") && trimmed.length() >= 2) {
            return trimmed;
        }
        if (trimmed.matches("^[+-]?\\d+(\\.\\d+)?([KMGTP]?)$")) {
            return trimmed;
        }
        if (trimmed.matches("(?i)^(true|false|immediate|deferred|memory|spfile|both)$")) {
            return trimmed.toUpperCase();
        }
        return "'" + trimmed.replace("'", "''") + "'";
    }
}
