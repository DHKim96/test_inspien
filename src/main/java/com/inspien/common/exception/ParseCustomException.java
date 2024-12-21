package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

/**
 * 데이터 파싱 중 발생하는 예외를 처리하기 위한 클래스.
 */
public class ParseCustomException extends AbstractProcessException {

    /**
     * 에러 코드와 단일 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수
     */
    public ParseCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    /**
     * 에러 코드와 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param cause   예외 원인
     */
    public ParseCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    /**
     * 에러 코드, 메시지 매개변수, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수
     * @param cause   예외 원인
     */
    public ParseCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, new Object[]{args}, cause);
    }


    /**
     * 에러 코드, 다중 매개변수, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수 배열
     * @param cause   예외 원인
     */
    public ParseCustomException(ErrCode errCode, Object[] args, Throwable cause) {
        super(errCode, args, cause);
    }
}
