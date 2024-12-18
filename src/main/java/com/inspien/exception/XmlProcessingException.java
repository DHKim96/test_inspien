package com.inspien.exception;

public class XmlProcessingException extends RuntimeException {
    public XmlProcessingException(String message) {
        super(message);
    }
    public XmlProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
