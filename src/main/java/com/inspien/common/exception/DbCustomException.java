package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class DbCustomException extends AbstractProcessException {
    public DbCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public DbCustomException(ErrCode errCode,Throwable cause, String args) {
        super(errCode, cause, args);
    }
}
