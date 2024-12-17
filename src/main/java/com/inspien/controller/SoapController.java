package com.inspien.controller;

import com.inspien.model.dto.*;
import com.inspien.service.SoapService;
import com.inspien.service.SoapServiceImpl;
import com.sun.mail.imap.protocol.Item;
import jakarta.xml.soap.SOAPException;

import java.util.List;

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

        System.out.println(soapResponse.getXmlData());

        // 1. xml 핸들링
        List<OrderInsert> orderInserts = soapService.handleXmlDatas(soapResponse.getXmlData());

        // 2. 핸들링한 xml 데이터 DBMS에 insert
        int insertOrders = soapService.insertOrderList(orderInserts);

        if (insertOrders > 0) {
            System.out.println("성공했습니다.");
        };

        // 3. json 핸들링

        // 4. 핸들링한 json 데이터 FTP 서버에 insert
    }

}
