/**
 * 多数据库支持：每种库一个 {@link com.dbboys.api.DatabaseDialect} 实现（通常放在子包如 {@code gbase}、{@code oracle}），
 * 在 {@link DatabaseDialectRegistry#createDefault()} 中注册；应用其它处只依赖 {@code Connect.getDbtype()} + 注册表解析。
 */
package com.dbboys.impl.dialect;
