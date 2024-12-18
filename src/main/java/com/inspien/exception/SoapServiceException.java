package com.inspien.exception;

public class SoapServiceException extends Exception {

    public SoapServiceException(String message) {
        super(message);
    }

    public SoapServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
