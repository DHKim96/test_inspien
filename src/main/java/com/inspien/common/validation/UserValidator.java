package com.inspien.common.validation;

import com.inspien.soap.dto.User;

import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    private final String NAME_PATTERN;
    private final String PHONE_PATTERN;
    private final String EMAIL_PATTERN;

    public UserValidator() {
        NAME_PATTERN = "^[a-zA-Z가-힣]+$";
        PHONE_PATTERN = "^\\d{3}-\\d{4}-\\d{4}$";
        EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    }

    @Override
    public void validate(User user) {
        this.validateName(user.getName());
        this.validatePhone(user.getPhone());
        this.validateEmail(user.getEmail());
    }

    public void validateName(String name) {
        if (name == null || !Pattern.matches(NAME_PATTERN, name)) {
            throw new IllegalArgumentException("USER 이름이 유효하지 않습니다. :" + name);
        }
    }

    public void validatePhone(String phone) {
        if (phone == null || !Pattern.matches(PHONE_PATTERN, phone)) {
            throw new IllegalArgumentException("USER 전화번호가 유효하지 않습니다. :" + phone);
        }
    }

    public void validateEmail(String email) {
        if (email == null || !Pattern.matches(EMAIL_PATTERN, email)) {
            throw new IllegalArgumentException("USER 이메일이 유효하지 않습니다." + email);
        }
    }
}
