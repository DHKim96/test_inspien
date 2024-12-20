package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class FtpCustomException extends AbstractProcessException {
    public FtpCustomException(ErrCode errCode) {
        super(errCode);
    }

    public FtpCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public FtpCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    public FtpCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, args, cause);
    }
}
