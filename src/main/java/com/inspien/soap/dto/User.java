package com.inspien.soap.dto;

import lombok.Builder;
import lombok.Data;

/**
 * SOAP 요청에 사용되는 사용자 정보를 저장하는 DTO 클래스.
 * <p>
 * 주요 필드:
 * <ul>
 *     <li>name - 사용자의 이름</li>
 *     <li>phone - 사용자의 전화번호</li>
 *     <li>email - 사용자의 이메일 주소</li>
 * </ul>
 */
@Data
@Builder
public class User {
    private String name;
    private String phone;
    private String email;
}
