package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class XmlCustomException extends AbstractProcessException {
    public XmlCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public XmlCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    public XmlCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, args, cause);
    }
}
