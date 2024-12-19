package com.inspien.soap.controller;

import com.inspien.common.exception.SoapCustomException;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import com.inspien.soap.service.SoapService;
import com.inspien.soap.service.SoapServiceImpl;

public class SoapController {
    private final SoapService soapService;

    public SoapController() {
        this.soapService = new SoapServiceImpl();
    }

    public SoapResponse executeService(User user) {

        SoapResponse soapResponse = null;

        try {
            // 1. endPoint 정보 로드
            String endPoint = this.loadEndPoint();
            // 2. SOAP Request
            String response = this.requestSoapWebService(user, endPoint);
            // 3. SOAP Response 파싱
            soapResponse = this.parseSoapResponse(response);

        } catch (SoapCustomException e){
            e.printStackTrace();
        }

        return soapResponse;
    }

    private String loadEndPoint() throws SoapCustomException {
        String endPoint = soapService.loadEndPoint();

        if (endPoint == null || endPoint.isEmpty()) {
            throw new SoapCustomException("EndPoint 정보가 비어있습니다.");
        }

        return endPoint;
    }


    private String requestSoapWebService(User user, String endPoint) throws SoapCustomException {
        String response = soapService.requestSoapWebService(user, endPoint);

        if (response == null || response.isEmpty()) {
            throw new SoapCustomException("SOAP Response 정보가 비어있습니다.");
        }

        return response;
    }

    private SoapResponse parseSoapResponse(String response) throws SoapCustomException {

        SoapResponse soapResponse = soapService.parseSoapXML(response);

        if (soapResponse == null) {
            throw new SoapCustomException("파싱한 soapResponse 정보가 비어있습니다.");
        }

        return soapResponse;
    }


}
