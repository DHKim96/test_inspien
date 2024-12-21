package com.inspien.common.exception;

import com.inspien.common.util.ErrCode;

/**
 * 데이터베이스 관련 예외를 처리하기 위한 커스텀 예외 클래스.
 * <p>
 * 데이터베이스 작업 중 발생하는 다양한 에러 상황을 처리합니다.
 */
public class DbCustomException extends AbstractProcessException {

    /**
     * 에러 코드와 단일 매개변수를 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 단일 매개변수
     */
    public DbCustomException(ErrCode errCode, String args) {
        super(errCode, args);
    }

    /**
     * 에러 코드와 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param cause   예외 원인
     */
    public DbCustomException(ErrCode errCode, Throwable cause) {
        super(errCode, cause);
    }

    /**
     * 에러 코드, 단일 매개변수, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode 에러 코드
     * @param args    메시지 포맷팅에 사용할 단일 매개변수
     * @param cause   예외 원인
     */
    public DbCustomException(ErrCode errCode, String args, Throwable cause) {
        super(errCode, new Object[]{args}, cause);
    }

    /**
     * 에러 코드와 테이블 이름 및 컬럼 이름을 기반으로 예외를 생성합니다.
     *
     * @param errCode    에러 코드
     * @param tableName  테이블 이름
     * @param columnName 컬럼 이름
     */
    public DbCustomException(ErrCode errCode, String tableName, String columnName) {
        this(errCode, tableName, columnName, null);
    }

    /**
     * 에러 코드, 테이블 이름, 컬럼 이름, 원인을 기반으로 예외를 생성합니다.
     *
     * @param errCode    에러 코드
     * @param tableName  테이블 이름
     * @param columnName 컬럼 이름
     * @param cause      예외 원인
     */
    public DbCustomException(ErrCode errCode, String tableName, String columnName, Throwable cause) {
        super(errCode, new Object[]{tableName, columnName}, cause);
    }
}
