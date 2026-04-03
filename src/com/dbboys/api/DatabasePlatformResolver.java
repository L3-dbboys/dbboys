package com.dbboys.api;

import com.dbboys.vo.Connect;

import java.util.Collection;
import java.util.Optional;

/**
 * 多数据库统一解析入口。
 * 对上层隐藏 dbtype 到平台实现的路由细节，避免业务层分别依赖多种 provider 接口。
 */
public interface DatabasePlatformResolver {

    MetadataRepository metadata(Connect connect);

    SqlexeRepository sqlexe(Connect connect);

    DdlRepository ddl(Connect connect);

    InstanceAdminRepository admin(Connect connect);

    Collection<DatabasePlatform> allPlatforms();

    DatabasePlatform getPlatform(String dbType);

    DatabasePlatform requirePlatform(Connect connect);

    DatabasePlatform requirePlatform(String dbType);

    default <T> Optional<T> capability(Connect connect, Class<T> type) {
        if (connect == null) {
            return Optional.empty();
        }
        return capability(connect.getDbtype(), type);
    }

    default <T> Optional<T> capability(String dbType, Class<T> type) {
        DatabasePlatform platform = getPlatform(dbType);
        if (platform == null) {
            return Optional.empty();
        }
        return platform.capability(type);
    }
}
