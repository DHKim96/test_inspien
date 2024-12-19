package com.inspien.common.util;

import java.lang.reflect.InvocationTargetException;

public class ValidationUtil {
    public static <T extends RuntimeException> void validateNotNull(Object object, String fieldName, Class<T> exceptionClass) throws T {
        // 체크 예외인 Exception 을 상속하면 안됨

        if (object == null) {
            try {
                throw exceptionClass
                        .getConstructor(String.class)
                        .newInstance(String.format("%s 값이 비어 있습니다.", fieldName));
            } catch (ReflectiveOperationException e){
                throw new RuntimeException("예외 생성 중 오류가 발생했습니다.", e);
            }
        }
    }
}
