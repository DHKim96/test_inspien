package com.inspien.exception;

public class InsJsonProcessingException extends RuntimeException {
    public InsJsonProcessingException(String message) {
        super(message);
    }
    public InsJsonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
