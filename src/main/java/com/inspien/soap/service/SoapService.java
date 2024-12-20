package com.inspien.soap.service;

import com.inspien.common.exception.ParseCustomException;
import com.inspien.common.exception.SoapCustomException;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

/**
 * SOAP 통신을 처리하기 위한 인터페이스.
 */
public interface SoapService {

    /**
     * SOAP 요청을 수행하고 응답 데이터를 문자열로 반환합니다.
     *
     * @param user      요청에 필요한 유저 데이터
     * @param endPoint  SOAP 서비스 엔드포인트 URL
     * @return          SOAP 응답 데이터 (String 형태)
     * @throws SoapCustomException SOAP 요청 중 오류가 발생한 경우
     */
    String requestSoapWebService(User user, String endPoint) throws SoapCustomException;

    /**
     * SOAP 응답 데이터를 파싱하여 SoapResponse 객체로 반환합니다.
     *
     * @param res   SOAP 응답 데이터 (String 형태)
     * @return      SoapResponse 객체
     * @throws SoapCustomException SOAP 응답 처리 중 오류가 발생한 경우
     * @throws ParseCustomException XML 데이터 파싱 중 오류가 발생한 경우
     */
    SoapResponse parseSoapXML(String res) throws SoapCustomException, ParseCustomException;

    /**
     * SOAP 서비스 엔드포인트를 로드하여 반환합니다.
     *
     * @return SOAP 서비스 엔드포인트 URL
     * @throws SoapCustomException 엔드포인트 로드 중 오류가 발생한 경우
     */
    String loadEndPoint() throws SoapCustomException;
}
