package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class SoapCustomException extends AbstractProcessException {
    public SoapCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public SoapCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    public SoapCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, args, cause);
    }
}
