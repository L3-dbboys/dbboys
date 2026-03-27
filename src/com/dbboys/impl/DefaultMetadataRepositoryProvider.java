package com.dbboys.impl;

import com.dbboys.api.MetadataRepository;
import com.dbboys.api.MetadataRepositoryProvider;
import com.dbboys.impl.dialect.DatabaseDialectRegistry;
import com.dbboys.vo.Connect;

/**
 * 按 {@link Connect#getDbtype()} 从 {@link DatabaseDialectRegistry} 取元数据实现（与 {@link DefaultSqlexeRepositoryProvider} 成对，因 Java 接口返回类型不能合并为一个 {@code get}）。
 */
public final class DefaultMetadataRepositoryProvider implements MetadataRepositoryProvider {

    private final DatabaseDialectRegistry registry;

    public DefaultMetadataRepositoryProvider(DatabaseDialectRegistry registry) {
        this.registry = registry;
    }

    @Override
    public MetadataRepository get(Connect connect) {
        return registry.requireDialect(connect).getMetadataRepository();
    }
}
