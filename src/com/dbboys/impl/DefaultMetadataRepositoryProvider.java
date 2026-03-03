package com.dbboys.impl;

import com.dbboys.api.DatabaseDialect;
import com.dbboys.api.MetadataRepository;
import com.dbboys.api.MetadataRepositoryProvider;
import com.dbboys.impl.dialect.DatabaseDialectRegistry;
import com.dbboys.vo.Connect;

public final class DefaultMetadataRepositoryProvider implements MetadataRepositoryProvider {

    private final DatabaseDialectRegistry registry;

    public DefaultMetadataRepositoryProvider(DatabaseDialectRegistry registry) {
        this.registry = registry;
    }

    @Override
    public MetadataRepository get(Connect connect) {
        DatabaseDialect dialect = registry.getDialect(connect.getDbtype());
        if (dialect == null) {
            throw new IllegalArgumentException("Unsupported database type: " + connect.getDbtype());
        }
        return dialect.getMetadataRepository();
    }
}
