package com.inspien.controller;

import com.inspien.model.dto.*;
import com.inspien.model.dto.Record;
import com.inspien.service.SoapService;
import com.inspien.service.SoapServiceImpl;
import jakarta.xml.soap.SOAPException;

import java.util.List;
import java.util.Map;

public class SoapController {
    private final SoapService soapService;

    public SoapController() {
        this.soapService = new SoapServiceImpl();
    }

    public void executeService(String name, String phone, String email) throws SOAPException {

        String res = soapService.requestSoapWebService(
                                            User.builder()
                                                .name(name)
                                                .phone(phone)
                                                .email(email)
                                            .build()
                                            );

        SoapResponse soapResponse = soapService.parseSoapXML(res);

        System.out.println(soapResponse.getDbConnInfo());

        Map<String, String> dbconfig = soapService.connInfoToMap(soapResponse.getDbConnInfo(), 1);

        // 1. xml 핸들링
        List<OrderInsert> orderInserts = soapService.handleXmlDatas(soapResponse.getXmlData());

        // 2. 핸들링한 xml 데이터 DBMS에 insert
        int insertOrders = soapService.insertOrderList(orderInserts, dbconfig);
        
        if (insertOrders > 0) {
            System.out.println("dbms insert 성공");
        }

        // 3. json 핸들링
        List<Record> records = soapService.handleJsonDatas(soapResponse.getJsonData());

        // 4. 핸들링한 json 데이터 FTP 서버에 insert

        // 4.1. 파일 생성
        String filepath = "C:\\workspace\\inspien\\src\\main\\resources\\INSPIEN_JSON_[김동현]_[20241218].txt ";
        soapService.convertToFlatFile(records, filepath);

        // 4.2. FTP 서버에 insert
        Map<String, String> ftpconfig = soapService.connInfoToMap(soapResponse.getFtpConnInfo(), 2);

    }

}
