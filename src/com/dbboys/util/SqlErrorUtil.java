package com.dbboys.util;

import com.dbboys.api.ChangeDatabaseFailureKind;
import com.dbboys.app.AppContext;
import com.dbboys.impl.DialectServices;
import com.dbboys.vo.Connect;

import java.sql.SQLException;

/**
 * 基于连接方言分类 JDBC 错误。
 */
public final class SqlErrorUtil {

    private SqlErrorUtil() {
    }

    public static boolean isDisconnectError(Connect connect, SQLException e) {
        if (connect == null || e == null) {
            return false;
        }
        try {
            return resolveDialectServices()
                    .requireDialect(connect)
                    .classifyChangeDatabaseFailure(e) == ChangeDatabaseFailureKind.DISCONNECTED;
        } catch (Exception ex) {
            return false;
        }
    }

    private static DialectServices resolveDialectServices() {
        try {
            return AppContext.get(DialectServices.class);
        } catch (IllegalStateException e) {
            return DialectServices.createDefault();
        }
    }
}
