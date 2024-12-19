package com.inspien.common.exception;

public abstract class AssignmentProcessException extends Exception {
    private final String code;

    protected AssignmentProcessException(ErrCode errCode, Object... args) {
        super(String.format(errCode.getMsg(), args));
        this.code = errCode.getCode();
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Error Code: " + code + ", Message: " + getMessage();
    }
}
