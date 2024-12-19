package com.inspien.common.validation;

import com.inspien.json.dto.RecordResponse;

import java.util.regex.Pattern;

public class RecordValidator implements Validator<RecordResponse> {
    private final String NAME_PATTERN;
    private final String PHONE_PATTERN;
    private final String EMAIL_PATTERN;
    private final String BIRTH_PATTERN;
    private final String COMPANY_PATTERN;
    private final String PERSONAL_NUMBER_PATTERN;
    private final String ORGANIZATION_NUMBER_PATTERN;
    private final String COUNTRY_PATTERN;
    private final String REGION_CITY_PATTERN;
    private final String STREET_PATTERN;
    private final String ZIPCODE_PATTERN;
    private final String CREDIT_CARD_PATTERN;
    private final String GUID_PATTERN;

    public RecordValidator() {
        NAME_PATTERN = "^[a-zA-ZÀ-ÖØ-öø-ÿ가-힣 ]+$"; // 숫자 및 특수문자 제외
        PHONE_PATTERN = "^[+]{1}[0-9]{9}$";
        EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"; // 표준 이메일 형식
        BIRTH_PATTERN = "^\\d{4}/\\d{2}/\\d{2}$"; // yyyy/MM/dd
        COMPANY_PATTERN = "^[a-zA-Z0-9 .,'-]+$"; // 알파벳, 공백, 특수 문자 포함
        PERSONAL_NUMBER_PATTERN = "^\\d{8} \\d{4}$"; // 숫자 그룹 간 공백 포함
        ORGANIZATION_NUMBER_PATTERN = "^\\d{8} \\d{4}$"; // 숫자와 하이픈 포함
        COUNTRY_PATTERN = "^[a-zA-Z ,.'-]+$";
        REGION_CITY_PATTERN = "^[a-zA-Z ,.'-]+$"; // 영문, 특문 허용
        STREET_PATTERN = "^[a-zA-Z0-9 ,.'#-]+$"; // 영문, 특문, 숫자 허용
        ZIPCODE_PATTERN = "^[a-zA-Z0-9 -]+$"; // 숫자, 공백 또는 하이픈 포함
        CREDIT_CARD_PATTERN =  "^(\\d{4} ?){3,4}\\d{4}$";
        GUID_PATTERN = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$"; // 표준 GUID 형식
    }

    @Override
    public void validate(RecordResponse record) {
        validateName(record.getNames());
        validatePhone(record.getPhone());
        validateEmail(record.getEmail());
        validateBirthday(record.getBirthday());
        validateCompany(record.getCompany());
        validatePersonalNumber(record.getPersonalNumber());
        validateOrganizationNumber(record.getOrganizationNumber());
        validateCountry(record.getCountry());
        validateRegionCity(record.getRegion());
        validateRegionCity(record.getCity());
        validateStreet(record.getStreet());
        validateZipCode(record.getZipCode());
        validateCreditCard(record.getCreditCard());
        validateGuid(record.getGuid());
    }

    private void validateName(String name) {
        if (name == null || !Pattern.matches(NAME_PATTERN, name)) {
            throw new IllegalArgumentException("RECORD 이름이 유효하지 않습니다: " + name);
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || !Pattern.matches(PHONE_PATTERN, phone)) {
            throw new IllegalArgumentException("RECORD 전화번호가 유효하지 않습니다: " + phone);
        }
    }

    private void validateEmail(String email) {
        if (email == null || !Pattern.matches(EMAIL_PATTERN, email)) {
            throw new IllegalArgumentException("RECORD 이메일이 유효하지 않습니다: " + email);
        }
    }

    private void validateBirthday(String birthday) {
        if (birthday == null || !Pattern.matches(BIRTH_PATTERN, birthday)) {
            throw new IllegalArgumentException("RECORD 생년월일이 유효하지 않습니다: " + birthday);
        }
    }

    private void validateCompany(String company) {
        if (company == null || !Pattern.matches(COMPANY_PATTERN, company)) {
            throw new IllegalArgumentException("RECORD 회사명이 유효하지 않습니다: " + company);
        }
    }

    private void validatePersonalNumber(String personalNumber) {
        if (personalNumber == null || !Pattern.matches(PERSONAL_NUMBER_PATTERN, personalNumber)) {
            throw new IllegalArgumentException("RECORD 개인번호가 유효하지 않습니다: " + personalNumber);
        }
    }

    private void validateOrganizationNumber(String organizationNumber) {
        if (organizationNumber == null || !Pattern.matches(ORGANIZATION_NUMBER_PATTERN, organizationNumber)) {
            throw new IllegalArgumentException("RECORD 조직번호가 유효하지 않습니다: " + organizationNumber);
        }
    }

    private void validateCountry(String country) {
        if (country == null || !Pattern.matches(COUNTRY_PATTERN, country)) {
            throw new IllegalArgumentException("RECORD 국가명이 유효하지 않습니다: " + country);
        }
    }

    private void validateRegionCity(String regionCity) {
        if (regionCity == null || !Pattern.matches(REGION_CITY_PATTERN, regionCity)) {
            throw new IllegalArgumentException("RECORD 지역/도시명이 유효하지 않습니다: " + regionCity);
        }
    }

    private void validateStreet(String street) {
        if (street == null || !Pattern.matches(STREET_PATTERN, street)) {
            throw new IllegalArgumentException("RECORD 인스턴스에 주소가 유효하지 않습니다: " + street);
        }
    }

    private void validateZipCode(String zipCode) {
        if (zipCode == null || !Pattern.matches(ZIPCODE_PATTERN, zipCode)) {
            throw new IllegalArgumentException("RECORD 인스턴스에 우편번호가 유효하지 않습니다: " + zipCode);
        }
    }

    private void validateCreditCard(String creditCard) {
        if (creditCard == null || !Pattern.matches(CREDIT_CARD_PATTERN, creditCard)) {
            throw new IllegalArgumentException("RECORD 인스턴스에 신용카드 번호가 유효하지 않습니다: " + creditCard);
        }
    }

    private void validateGuid(String guid) {
        if (guid == null || !Pattern.matches(GUID_PATTERN, guid)) {
            throw new IllegalArgumentException("RECORD 인스턴스에 GUID가 유효하지 않습니다: " + guid);
        }
    }
}
