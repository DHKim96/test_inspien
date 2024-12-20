package com.inspien.common.validation;

import com.inspien.common.exception.SoapCustomException;
import com.inspien.common.util.ErrCode;
import com.inspien.soap.dto.User;

import java.util.regex.Pattern;

/**
 * {@link User} 객체를 검증하는 클래스.
 * <p>
 * 주요 검증 항목:
 * <ul>
 *     <li>이름: 알파벳 또는 한글로 구성</li>
 *     <li>전화번호: 000-0000-0000 형식</li>
 *     <li>이메일: 이메일 주소 형식</li>
 * </ul>
 */
public class UserValidator implements Validator<User> {
    private final String NAME_PATTERN;
    private final String PHONE_PATTERN;
    private final String EMAIL_PATTERN;

    /**
     * 기본 생성자.
     * 정규식을 초기화합니다.
     */
    public UserValidator() {
        NAME_PATTERN = "^[a-zA-Z가-힣]+$";
        PHONE_PATTERN = "^\\d{3}-\\d{4}-\\d{4}$";
        EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    }

    /**
     * {@link User} 객체를 검증합니다.
     *
     * @param user 검증할 사용자 객체
     * @throws SoapCustomException 사용자의 정보가 유효하지 않은 경우 발생
     */
    @Override
    public void validate(User user) throws SoapCustomException {
        this.validateName(user.getName());
        this.validatePhone(user.getPhone());
        this.validateEmail(user.getEmail());
    }

    /**
     * 이름을 검증합니다.
     *
     * @param name 검증할 이름
     * @throws SoapCustomException 이름이 null이거나 형식이 유효하지 않은 경우 발생
     */
    public void validateName(String name) throws SoapCustomException {
        if (name == null || !Pattern.matches(NAME_PATTERN, name)) {
            throw new SoapCustomException(ErrCode.INVALID_FORMAT, "USER_NAME");
        }
    }

    /**
     * 전화번호를 검증합니다.
     *
     * @param phone 검증할 전화번호
     * @throws SoapCustomException 전화번호가 null이거나 형식이 유효하지 않은 경우 발생
     */
    public void validatePhone(String phone) throws SoapCustomException {
        if (phone == null || !Pattern.matches(PHONE_PATTERN, phone)) {
            throw new SoapCustomException(ErrCode.INVALID_FORMAT, "USER_PHONE");
        }
    }

    /**
     * 이메일을 검증합니다.
     *
     * @param email 검증할 이메일
     * @throws SoapCustomException 이메일이 null이거나 형식이 유효하지 않은 경우 발생
     */
    public void validateEmail(String email) throws SoapCustomException {
        if (email == null || !Pattern.matches(EMAIL_PATTERN, email)) {
            throw new SoapCustomException(ErrCode.INVALID_FORMAT, "EMAIL");
        }
    }
}
