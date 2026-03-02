package com.dbboys.app.exception;

public class SqlExecutionException extends DbBoysException {
    public SqlExecutionException(String message) {
        super(message);
    }

    public SqlExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlExecutionException(Throwable cause) {
        super(cause);
    }
}
