package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class ParseCustomException extends AbstractProcessException {

    public ParseCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public ParseCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    public ParseCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, args, cause);
    }
}
