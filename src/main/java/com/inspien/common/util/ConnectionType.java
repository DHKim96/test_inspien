package com.inspien.common.util;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ConnectionType {
    DATABASE(new String[]{"host", "port", "sid", "user", "password", "tablename"}),
    FTP(new String[]{"host", "port", "user", "password", "filepath"});

    private final String[] keys;

    ConnectionType(String[] keys) {
        this.keys = keys;
    }

    // 특정 타입이 enum에 존재하는지 확인하는 메서드
    public static boolean isValidType(String inputType) {
       return Arrays.stream(ConnectionType.values()).anyMatch(v -> v.name().equals(inputType.toUpperCase()));
    }
}
