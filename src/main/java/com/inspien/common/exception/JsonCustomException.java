package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

/**
 * JSON 처리와 관련된 예외를 나타내는 클래스.
 * <p>
 * JSON 데이터 파싱 및 변환 과정에서 발생하는 예외를 처리합니다.
 */
public class JsonCustomException extends AbstractProcessException {

    /**
     * 에러 코드와 예외 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param cause   예외 원인
     */
    public JsonCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    /**
     * 에러 코드와 메시지 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수
     */
    public JsonCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    /**
     * 에러 코드와 메시지 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args1   메시지 포맷팅에 사용할 매개변수1
     * @param args2   메시지 포맷팅에 사용할 매개변수2
     */
    public JsonCustomException(ErrCode errCode, String args1, String args2) {
        super(errCode, new Object[]{args1, args2}, null);
    }

    /**
     * 에러 코드, 메시지 매개변수, 예외 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수
     * @param cause   예외 원인
     */
    public JsonCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, new Object[]{args}, cause);
    }
}
