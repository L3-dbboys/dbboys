package com.dbboys.impl;

import com.dbboys.api.SqlexeRepository;
import com.dbboys.api.SqlexeRepositoryProvider;
import com.dbboys.impl.dialect.DatabaseDialectRegistry;
import com.dbboys.vo.Connect;

/**
 * 按 {@link Connect#getDbtype()} 从 {@link DatabaseDialectRegistry} 取 SQL/切库实现（与 {@link DefaultMetadataRepositoryProvider} 成对）。
 */
public final class DefaultSqlexeRepositoryProvider implements SqlexeRepositoryProvider {

    private final DatabaseDialectRegistry registry;

    public DefaultSqlexeRepositoryProvider(DatabaseDialectRegistry registry) {
        this.registry = registry;
    }

    @Override
    public SqlexeRepository get(Connect connect) {
        return registry.requireDialect(connect).getSqlexeRepository();
    }
}
