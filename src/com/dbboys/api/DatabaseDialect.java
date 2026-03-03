package com.dbboys.api;

import com.dbboys.vo.Connect;

import java.sql.Connection;

/**
 * 数据库方言：按数据库类型提供建连参数和会话初始化。
 * 阶段 1 仅负责连接创建与会话初始化；元数据/执行按库切换在阶段 2。
 */
public interface DatabaseDialect {

    /**
     * 本方言对应的数据库类型标识，与 {@link Connect#getDbtype()} 一致，如 "GBASE 8S"、"oracle"。
     */
    String getDbType();

    /**
     * 返回该库的 JDBC 连接参数，由 {@link ConnectionService} 实现类用于加载驱动并建连。
     */
    ConnectionParams getConnectionParams(Connect connect) throws Exception;

    /**
     * 在已建立的连接上做会话级初始化（如 GBase 的 sqlmode、Oracle 的 schema）。
     * 不支持时可空实现。
     */
    void sessionInit(Connection conn, Connect connect) throws Exception;

    /**
     * 是否支持会话初始化。若为 false，{@link ConnectionService#getGbaseModeConnection} 等价于普通建连。
     */
    default boolean supportsSessionInit() {
        return true;
    }

    /**
     * 按数据库方言调整连接属性（例如 GBase 的 DB_LOCALE 编码转换）。
     * 默认实现为原样返回 {@link Connect#getProps()}。
     */
    default String adjustProps(Connect connect, String dbLocale) {
        return connect != null ? connect.getProps() : null;
    }

    /**
     * 该库的元数据访问实现。阶段 2 按 Connect 的 dbtype 通过 Provider 获取。
     */
    com.dbboys.api.MetadataRepository getMetadataRepository();

    /**
     * 该库的 SQL 执行/模式实现（如 setDatabase、getSqlMode）。阶段 2 按 Connect 的 dbtype 通过 Provider 获取。
     */
    com.dbboys.api.SqlexeRepository getSqlexeRepository();

    /**
     * JDBC 连接参数：URL、驱动类名、驱动 jar 路径。
     */
    final class ConnectionParams {
        private final String url;
        private final String driverClassName;
        private final String jarFilePath;

        public ConnectionParams(String url, String driverClassName, String jarFilePath) {
            this.url = url;
            this.driverClassName = driverClassName;
            this.jarFilePath = jarFilePath;
        }

        public String getUrl() {
            return url;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public String getJarFilePath() {
            return jarFilePath;
        }
    }
}
