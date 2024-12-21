package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

/**
 * FTP 관련 예외 처리 클래스.
 * <p>
 * FTP 작업 중 발생하는 다양한 예외를 처리합니다.
 */
public class FtpCustomException extends AbstractProcessException {

    /**
     * 에러 코드만으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     */
    public FtpCustomException(ErrCode errCode) {
        super(errCode);
    }

    /**
     * 에러 코드와 메시지 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수
     */
    public FtpCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    /**
     * 에러 코드와 예외 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param cause   예외 원인
     */
    public FtpCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    /**
     * 에러 코드, 메시지 매개변수, 예외 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 매개변수
     * @param cause   예외 원인
     */
    public FtpCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, new Object[]{args}, cause);
    }
}
