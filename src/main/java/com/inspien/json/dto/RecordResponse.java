package com.inspien.json.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSON 데이터를 매핑하기 위한 DTO 클래스.
 * <p>
 * 주요 필드:
 * <ul>
 *     <li>names - 사용자 이름</li>
 *     <li>phone - 사용자 전화번호</li>
 *     <li>email - 사용자 이메일 주소</li>
 *     <li>birthday - 사용자 생년월일</li>
 *     <li>company - 회사 이름</li>
 *     <li>personalNumber - 개인 식별 번호</li>
 *     <li>organizationNumber - 조직 식별 번호</li>
 *     <li>country - 국가</li>
 *     <li>region - 지역</li>
 *     <li>city - 도시</li>
 *     <li>street - 거리</li>
 *     <li>zipCode - 우편번호</li>
 *     <li>creditCard - 신용카드 정보</li>
 *     <li>guid - 고유 식별자</li>
 * </ul>
 */
@Data
public class RecordResponse {
    @JsonProperty("Names")
    private String names;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("BirthDate")
    private String birthday;

    @JsonProperty("Company")
    private String company;

    @JsonProperty("PersonalNumber")
    private String personalNumber;

    @JsonProperty("OrganisationNumber")
    private String organizationNumber;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Region")
    private String region;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Street")
    private String street;

    @JsonProperty("ZipCode")
    private String zipCode;

    @JsonProperty("CreditCard")
    private String creditCard;

    @JsonProperty("GUID")
    private String guid;
}
