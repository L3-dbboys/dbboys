package com.dbboys.app.exception;

public class DbBoysException extends RuntimeException {
    public DbBoysException(String message) {
        super(message);
    }

    public DbBoysException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbBoysException(Throwable cause) {
        super(cause);
    }
}
