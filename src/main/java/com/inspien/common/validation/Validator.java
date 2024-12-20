package com.inspien.common.validation;

import com.inspien.common.exception.SoapCustomException;

/**
 * 객체 검증을 위한 인터페이스.
 *
 * @param <T> 검증 대상 객체의 타입
 */
public interface Validator<T> {

    /**
     * 객체를 검증합니다.
     *
     * @param object 검증할 객체
     * @throws SoapCustomException 객체가 유효하지 않은 경우 발생
     */
    void validate(T object) throws SoapCustomException;
}
