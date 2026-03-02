package com.dbboys.app.exception;

import java.sql.SQLException;

public class ConnectionException extends DbBoysException {
    private final int errorCode;

    public ConnectionException(String message, SQLException cause) {
        super(message, cause);
        this.errorCode = cause.getErrorCode();
    }

    public ConnectionException(SQLException cause) {
        super(cause.getMessage(), cause);
        this.errorCode = cause.getErrorCode();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public boolean isDisconnected() {
        return errorCode == -79716 || errorCode == -79730;
    }
}
