package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;
import lombok.Getter;

/**
 * 애플리케이션의 공통 예외 처리 추상 클래스.
 * <p>
 * 에러 코드와 메시지를 관리하며 다양한 형태의 예외 생성자를 제공합니다.
 * 예외 처리 시 일관된 에러 코드 및 메시지를 사용할 수 있도록 설계되었습니다.
 */
@Getter
public abstract class AbstractProcessException extends Exception {
    private final String code;

    /**
     * 에러 코드만으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     */
    protected AbstractProcessException(ErrCode errCode) {
        this(errCode, null, null);
    }

    /**
     * 에러 코드와 단일 메시지 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 단일 매개변수
     */
    protected AbstractProcessException(ErrCode errCode, String args) {
        this(errCode, args != null ? new Object[]{args} : null, null);
    }

    /**
     * 에러 코드와 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param cause   예외 원인
     */
    protected AbstractProcessException(ErrCode errCode, Throwable cause) {
        this(errCode, null, cause);
    }

    /**
     * 에러 코드, 메시지 매개변수 배열, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수 배열
     * @param cause   예외 원인
     */
    protected AbstractProcessException(ErrCode errCode, Object[] args, Throwable cause) {
        super(args != null ? String.format(errCode.getMsg(), args) : errCode.getMsg(), cause);
        this.code = errCode.getCode();
    }

    @Override
    public String toString() {
        return "Error Code: " + code + ", Message: " + getMessage();
    }
}
