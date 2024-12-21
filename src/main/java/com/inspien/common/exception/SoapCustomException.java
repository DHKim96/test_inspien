package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

/**
 * SOAP 요청 및 응답 처리 중 발생하는 예외를 처리하는 클래스.
 */
public class SoapCustomException extends AbstractProcessException {

    /**
     * 에러 코드와 단일 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 단일 매개변수
     */
    public SoapCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    /**
     * 에러 코드와 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param cause   예외 원인
     */
    public SoapCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    /**
     * 에러 코드, 단일 메시지 매개변수, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 단일 매개변수
     * @param cause   예외 원인
     */
    public SoapCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, new Object[]{args}, cause);
    }

    /**
     * 에러 코드와 다중 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수 배열
     */
    public SoapCustomException(ErrCode errCode, Object[] args) {
        super(errCode, args, null);
    }

    /**
     * 에러 코드, 다중 매개변수, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수 배열
     * @param cause   예외 원인
     */
    public SoapCustomException(ErrCode errCode, Object[] args, Throwable cause) {
        super(errCode, args, cause);
    }
}
