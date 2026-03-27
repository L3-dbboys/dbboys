package com.dbboys.api;

import com.dbboys.vo.Connect;

/**
 * 按连接（dbtype）提供对应的实例管理仓库实现。
 */
public interface InstanceAdminRepositoryProvider {

    InstanceAdminRepository admin(Connect connect);
}
