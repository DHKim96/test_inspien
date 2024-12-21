package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

/**
 * XML 데이터 처리 중 발생하는 커스텀 예외 클래스.
 * <p>
 * XML 파싱 및 유효성 검사 중 발생하는 다양한 에러를 처리.
 */
public class XmlCustomException extends AbstractProcessException {

    /**
     * 에러 코드와 단일 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 단일 매개변수
     */
    public XmlCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    /**
     * 에러 코드와 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param cause   예외 원인
     */
    public XmlCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    /**
     * 에러 코드, 단일 매개변수, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 단일 매개변수
     * @param cause   예외 원인
     */
    public XmlCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, new Object[]{args}, cause);
    }

    /**
     * 에러 코드와 다중 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수 배열
     */
    public XmlCustomException(ErrCode errCode, Object[] args) {
        super(errCode, args, null);
    }

    /**
     * 에러 코드, 다중 매개변수, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수 배열
     * @param cause   예외 원인
     */
    public XmlCustomException(ErrCode errCode, Object[] args, Throwable cause) {
        super(errCode, args, cause);
    }
}
