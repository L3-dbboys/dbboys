package com.dbboys.api;

/**
 * 切换当前库（{@code change database} / 等价 JDBC 操作）失败时，由方言对 {@link java.sql.SQLException} 分类，
 * 供 {@link com.dbboys.api.ConnectionService#changeDefaultDatabase} 等选择重连或提示断连。
 */
public enum ChangeDatabaseFailureKind {

    /** 普通失败：将错误码/信息返回给界面 */
    OTHER,

    /** 连接已不可用，应提示用户重连 */
    DISCONNECTED,

    /** 应关闭当前连接、按新库参数重建连接后再切换 */
    RETRY_WITH_NEW_CONNECTION
}
