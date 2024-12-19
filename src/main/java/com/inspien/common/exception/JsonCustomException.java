package com.inspien.common.exception;

public class JsonCustomException extends RuntimeException {
    public JsonCustomException(String message) {
        super(message);
    }
    public JsonCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
