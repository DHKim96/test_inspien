package com.inspien.soap.controller;

import com.inspien.common.exception.ParseCustomException;
import com.inspien.common.exception.SoapCustomException;
import com.inspien.common.util.ErrCode;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import com.inspien.soap.service.SoapService;
import com.inspien.soap.service.SoapServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * SOAP 요청 및 응답 처리를 담당하는 컨트롤러 클래스.
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>SOAP 요청 생성 및 전송</li>
 *     <li>SOAP 응답 파싱</li>
 *     <li>SOAP 프로세스 수행</li>
 * </ul>
 */
@Slf4j
public class SoapController {
    private final SoapService soapService;

    /**
     * 기본 생성자.
     * SoapService 구현체를 초기화합니다.
     */
    public SoapController() {
        this.soapService = new SoapServiceImpl();
    }

    /**
     * SOAP 서비스 실행 메서드.
     * SOAP 요청을 전송하고 응답 데이터를 파싱하여 {@link SoapResponse} 객체로 반환합니다.
     *
     * @param user SOAP 요청에 사용되는 사용자 정보
     * @return SOAP 응답 데이터가 포함된 {@link SoapResponse} 객체
     * @throws SoapCustomException EndPoint 생성 혹은 SOAP 요청 중 예외가 발생하거나 응답 데이터가 유효하지 않을 경우
     * @throws ParseCustomException 데이터 파싱 시 예외가 발생한 경우
     */
    public SoapResponse executeService(User user) throws SoapCustomException, ParseCustomException {

        SoapResponse soapResponse = null;

        // 1. endPoint 정보 로드
        String endPoint = this.loadEndPoint();
        // 2. SOAP Request
        String response = this.requestSoapWebService(user, endPoint);
        // 3. SOAP Response 파싱
        soapResponse = this.parseSoapResponse(response);


        return soapResponse;
    }

    /**
     * SOAP EndPoint 정보를 로드합니다.
     *
     * @return 로드된 EndPoint 정보
     * @throws SoapCustomException EndPoint 로드 중 오류가 발생한 경우
     */
    private String loadEndPoint() throws SoapCustomException {
        String endPoint = soapService.loadEndPoint();

        if (endPoint == null || endPoint.isEmpty()) {
            throw new SoapCustomException(ErrCode.NULL_POINT_ERROR, "EndPoint");
        }

        return endPoint;
    }

    /**
     * SOAP WebService를 호출하고 응답 데이터를 반환합니다.
     *
     * @param user     SOAP 요청에 사용되는 사용자 정보
     * @param endPoint 호출할 SOAP EndPoint
     * @return SOAP 응답 데이터 (문자열 형태)
     * @throws SoapCustomException SOAP 요청 중 오류가 발생한 경우
     */
    private String requestSoapWebService(User user, String endPoint) throws SoapCustomException {
        String response = soapService.requestSoapWebService(user, endPoint);

        if (response == null || response.isEmpty()) {
            throw new SoapCustomException(ErrCode.NULL_POINT_ERROR, "SOAP Response");
        }

        return response;
    }

    /**
     * SOAP 응답 데이터를 파싱하여 {@link SoapResponse} 객체로 반환합니다.
     *
     * @param response SOAP 응답 데이터 (문자열 형태)
     * @return 파싱된 {@link SoapResponse} 객체
     * @throws SoapCustomException  SOAP 응답 데이터가 유효하지 않을 경우 발생
     * @throws ParseCustomException SOAP 응답 데이터 파싱 중 오류가 발생한 경우
     */
    private SoapResponse parseSoapResponse(String response) throws SoapCustomException, ParseCustomException {

        SoapResponse soapResponse = soapService.parseSoapXML(response);

        if (soapResponse == null) {
            throw new SoapCustomException(ErrCode.NULL_POINT_ERROR, "SoapResponse");
        }

        return soapResponse;
    }


}
