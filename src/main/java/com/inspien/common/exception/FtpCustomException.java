package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class FtpCustomException extends AbstractProcessException {
    public FtpCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }
}
