package com.inspien.common.exception;

public class XmlCustomException extends RuntimeException {
    public XmlCustomException(String message) {
        super(message);
    }
    public XmlCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
