package com.inspien.common.exception;

public class DbCustomException extends RuntimeException {
    public DbCustomException(String message) {
        super(message);
    }
    public DbCustomException(String message, Throwable cause) {
      super(message, cause);
    }
}
