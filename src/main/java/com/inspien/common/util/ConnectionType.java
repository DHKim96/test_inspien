package com.inspien.common.util;

import lombok.Getter;

import java.util.Arrays;

/**
 * 서버 연결 정보를 관리하는 열거형 클래스.
 * <p>
 * 주요 역할:
 * <ul>
 *     <li>연결 타입(DATABASE, FTP)에 따른 필수 키값 관리</li>
 *     <li>유효한 연결 타입인지 확인</li>
 * </ul>
 */
@Getter
public enum ConnectionType {

    /**
     * 데이터베이스 연결에 필요한 키값 정의.
     * 필수 키: host, port, sid, user, password, tablename
     */
    DATABASE(new String[]{"host", "port", "sid", "user", "password", "tablename"}),

    /**
     * FTP 연결에 필요한 키값 정의.
     * <p>
     * 필수 키: host, port, user, password, filepath
     */
    FTP(new String[]{"host", "port", "user", "password", "filepath"});

    private final String[] keys;

    /**
     * 열거형 타입에 필요한 키값을 초기화합니다.
     *
     * @param keys 연결 타입에 필요한 키값 배열
     */
    ConnectionType(String[] keys) {
        this.keys = keys;
    }


    /**
     * 입력된 타입이 유효한 연결 타입인지 확인합니다.
     *
     * @param inputType 확인할 연결 타입 문자열
     * @return 유효한 타입이면 {@code true}, 그렇지 않으면 {@code false}
     */
    public static boolean isValidType(String inputType) {
       return Arrays.stream(ConnectionType.values()).anyMatch(v -> v.name().equals(inputType.toUpperCase()));
    }
}
