package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class DbCustomException extends AbstractProcessException {

    public DbCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public DbCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    public DbCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, args, cause);
    }

    public DbCustomException(ErrCode errCode, String tableName, String column) {
        super(errCode, new String[]{tableName, column});
    }

    public DbCustomException(ErrCode errCode, String tableName, String column, Throwable cause) {
        super(errCode, new String[]{tableName, column}, cause);
    }
}
