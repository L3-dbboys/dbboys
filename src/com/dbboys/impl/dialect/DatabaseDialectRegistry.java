package com.dbboys.impl.dialect;

import com.dbboys.api.DatabaseDialect;
import com.dbboys.impl.dialect.gbase.GbaseDialect;
import com.dbboys.impl.dialect.oracle.OracleDialect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按数据库类型查找方言。阶段 1 在应用启动时注册 GBase、Oracle，{@link com.dbboys.impl.ConnectionServiceImpl} 建连时按 connect.getDbtype() 委托。
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
     * 创建默认注册表并注册内置方言（GBASE 8S、oracle）。
     */
    public static DatabaseDialectRegistry createDefault() {
        DatabaseDialectRegistry registry = new DatabaseDialectRegistry();
        registry.register(new GbaseDialect());
        registry.register(new OracleDialect());
        return registry;
    }
}
