package com.inspien.xml.controller;

import com.inspien.common.exception.XmlCustomException;
import com.inspien.common.util.CommonUtil;
import com.inspien.common.util.ConnectionType;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.xml.dto.OrderInsert;
import com.inspien.xml.service.XmlService;
import com.inspien.xml.service.XmlServiceImpl;

import java.util.List;
import java.util.Map;

public class XmlController {
    private final XmlService xmlService;

    public XmlController() {
        this.xmlService = new XmlServiceImpl();
    }

    public void processXmlData(SoapResponse soapResponse) {
        // 2.1. DB 연결 정보 매핑
        Map<String, String> dbconfig = this.parseDBConnectionInfoToMap(soapResponse);
        // 2.2. XML_DATA 핸들링
        List<OrderInsert> orderInserts = this.handleXmlData(soapResponse);
        // 2.3. 핸들링한 XML_DATA DB에 INSERT
        int result = this.insertOrderList(orderInserts, dbconfig);

        if (result == 1) {
            System.out.println("INSERT 성공");
        } else {
            System.out.println("INSERT 실패");
        }
    }

    private Map<String, String> parseDBConnectionInfoToMap(SoapResponse soapResponse){
        Map<String, String> dbconfig = null;

        try {
            dbconfig = CommonUtil.parseConnectionInfoToMap(soapResponse.getDbConnInfo(), ConnectionType.DATABASE);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        return dbconfig;
    }

    private List<OrderInsert> handleXmlData(SoapResponse soapResponse) {
        List<OrderInsert> orderInserts = null;

        try {
            orderInserts = xmlService.handleXmlData(soapResponse.getXmlData());
        } catch (XmlCustomException e) {
            e.printStackTrace();
        }

        return orderInserts;
    }

    private int insertOrderList(List<OrderInsert> orderInserts, Map<String, String> dbconfig) {
        int result = 0;

        try {
            result = xmlService.insertOrderList(orderInserts, dbconfig);
        } catch (XmlCustomException e){
            e.printStackTrace();
        }

        return result;
    }
}
