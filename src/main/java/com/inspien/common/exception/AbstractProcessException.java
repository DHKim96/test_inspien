package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;
import lombok.Getter;

@Getter
public abstract class AbstractProcessException extends Exception {
    private final String code;

    protected AbstractProcessException(ErrCode errCode, String args) {
        super(String.format(errCode.getMsg(), args));
        this.code = errCode.getCode();
    }

    protected AbstractProcessException(ErrCode errCode, Throwable cause, String args) {
        super(String.format(errCode.getMsg(), args), cause);
        this.code = errCode.getCode();
    }

    @Override
    public String toString() {
        return "Error Code: " + code + ", Message: " + getMessage();
    }
}
