package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

public class XmlCustomException extends AbstractProcessException {

    public XmlCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    public XmlCustomException(ErrCode errCode, Throwable cause, String args) {
        super(errCode, cause, args);
    }
}
