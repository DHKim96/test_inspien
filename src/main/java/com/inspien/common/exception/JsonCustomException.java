package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class JsonCustomException extends AbstractProcessException {

    public JsonCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public JsonCustomException(ErrCode errCode, Throwable cause, String args) {
        super(errCode, cause, args);
    }
}
