package com.inspien.common.exception;

public class SoapCustomException extends RuntimeException {

    public SoapCustomException(String message) {
        super(message);
    }

    public SoapCustomException(String message, Throwable cause) {
        super(message, cause);
    }

}
