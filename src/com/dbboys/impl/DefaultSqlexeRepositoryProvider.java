package com.dbboys.impl;

import com.dbboys.api.DatabaseDialect;
import com.dbboys.api.SqlexeRepository;
import com.dbboys.api.SqlexeRepositoryProvider;
import com.dbboys.impl.dialect.DatabaseDialectRegistry;
import com.dbboys.vo.Connect;

public final class DefaultSqlexeRepositoryProvider implements SqlexeRepositoryProvider {

    private final DatabaseDialectRegistry registry;

    public DefaultSqlexeRepositoryProvider(DatabaseDialectRegistry registry) {
        this.registry = registry;
    }

    @Override
    public SqlexeRepository get(Connect connect) {
        DatabaseDialect dialect = registry.getDialect(connect.getDbtype());
        if (dialect == null) {
            throw new IllegalArgumentException("Unsupported database type: " + connect.getDbtype());
        }
        return dialect.getSqlexeRepository();
    }
}
