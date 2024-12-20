package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class JsonCustomException extends AbstractProcessException {

    public JsonCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    public JsonCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public JsonCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, args, cause);
    }

    public JsonCustomException(ErrCode errCode, String args1, String args2) {
        super(errCode, new String[]{args1, args2});
    }
}
