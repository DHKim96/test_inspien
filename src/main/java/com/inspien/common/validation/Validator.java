package com.inspien.common.validation;

public interface Validator<T> {
    void validate(T object);
}
