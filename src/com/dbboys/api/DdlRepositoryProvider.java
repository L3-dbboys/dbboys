package com.dbboys.api;

import com.dbboys.vo.Connect;

/**
 * 按连接（dbtype）提供对应的 DDL 仓库实现。
 */
public interface DdlRepositoryProvider {

    DdlRepository ddl(Connect connect);
}
