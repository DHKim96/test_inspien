package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;
import lombok.Getter;

@Getter
public abstract class AbstractProcessException extends Exception {
    private String code;

    protected AbstractProcessException(Throwable cause) {
        super(cause);
    }

    protected AbstractProcessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    protected AbstractProcessException(String msg) {
        super(msg);
    }

    protected AbstractProcessException(ErrCode errCode) {
        super(errCode.getMsg());
        this.code = errCode.getCode();
    }

    protected AbstractProcessException(ErrCode errCode, String args) {
        super(String.format(errCode.getMsg(), args));
        this.code = errCode.getCode();
    }

    protected AbstractProcessException(ErrCode errCode, Throwable cause) {
        super(errCode.getMsg(), cause);
        this.code = errCode.getCode();
    }

    protected AbstractProcessException(ErrCode errCode, String args, Throwable cause) {
        super(String.format(errCode.getMsg(), args), cause);
        this.code = errCode.getCode();
    }

    protected AbstractProcessException(ErrCode errCode, Object[] args) {
        super(String.format(errCode.getMsg(), args));
        this.code = errCode.getCode();
    }

    protected AbstractProcessException(ErrCode errCode, Object[] args, Throwable cause) {
        super(String.format(errCode.getMsg(), args), cause);
        this.code = errCode.getCode();
    }


    @Override
    public String toString() {
        return "Error Code: " + code + ", Message: " + getMessage();
    }
}
