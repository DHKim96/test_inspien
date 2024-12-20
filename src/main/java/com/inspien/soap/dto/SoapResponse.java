package com.inspien.soap.dto;

import lombok.Builder;
import lombok.Data;

/**
 * SOAP 응답 데이터를 저장하는 DTO 클래스.
 * <p>
 * 주요 필드:
 * <ul>
 *     <li>xmlData - SOAP 응답의 XML 데이터</li>
 *     <li>jsonData - SOAP 응답의 JSON 데이터</li>
 *     <li>dbConnInfo - DB 연결 정보</li>
 *     <li>ftpConnInfo - FTP 연결 정보</li>
 * </ul>
 */
@Data
@Builder
public class SoapResponse {
    String xmlData;
    String jsonData;
    String dbConnInfo;
    String ftpConnInfo;
}
