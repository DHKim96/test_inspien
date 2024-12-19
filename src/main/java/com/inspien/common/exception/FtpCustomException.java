package com.inspien.common.exception;

public class FtpCustomException extends RuntimeException {
    public FtpCustomException(String message) {
        super(message);
    }
    public FtpCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
