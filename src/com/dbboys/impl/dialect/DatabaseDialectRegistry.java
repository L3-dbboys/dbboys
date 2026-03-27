package com.dbboys.impl.dialect;

import com.dbboys.api.DatabaseDialect;
import com.dbboys.impl.dialect.gbase.GbaseDialect;
import com.dbboys.impl.dialect.oracle.OracleDialect;
import com.dbboys.vo.Connect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多库核心入口：一种 {@link Connect#getDbtype()} 对应一个 {@link DatabaseDialect}（建连、元数据、SQL 执行等）。
 *
 * @see com.dbboys.impl.DialectServices
 * @see com.dbboys.impl.ConnectionServiceImpl
 */
public final class DatabaseDialectRegistry {

    private final Map<String, DatabaseDialect> dialectByDbType = new ConcurrentHashMap<>();

    public DatabaseDialectRegistry() {
    }

    public void register(DatabaseDialect dialect) {
        if (dialect == null || dialect.getDbType() == null || dialect.getDbType().isBlank()) {
            throw new IllegalArgumentException("dialect and getDbType() must be non-null and non-blank");
        }
        dialectByDbType.put(dialect.getDbType(), dialect);
    }

    public void register(String dbType, DatabaseDialect dialect) {
        if (dbType == null || dbType.isBlank() || dialect == null) {
            throw new IllegalArgumentException("dbType and dialect must be non-null and non-blank");
        }
        dialectByDbType.put(dbType, dialect);
    }

    /**
     * 按 Connect 的 dbtype 获取方言，不存在时返回 null。
     */
    public DatabaseDialect getDialect(String dbType) {
        return dbType == null ? null : dialectByDbType.get(dbType);
    }

    /**
     * 与 {@link #getDialect(String)} 相同，但未知类型时抛异常，便于调用方少写重复判断。
     */
    public DatabaseDialect requireDialect(Connect connect) {
        if (connect == null) {
            throw new IllegalArgumentException("connect is null");
        }
        return requireDialect(connect.getDbtype());
    }

    public DatabaseDialect requireDialect(String dbType) {
        DatabaseDialect d = getDialect(dbType);
        if (d == null) {
            throw new IllegalArgumentException("Unsupported database type: " + dbType);
        }
        return d;
    }

    /**
     * 创建默认注册表并注册内置方言（GBASE 8S、oracle）。
     */
    public static DatabaseDialectRegistry createDefault() {
        DatabaseDialectRegistry registry = new DatabaseDialectRegistry();
        registry.register(new GbaseDialect());
        registry.register(new OracleDialect());
        return registry;
    }
}
