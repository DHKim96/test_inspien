package com.inspien.service;

import com.inspien.model.dto.User;

import javax.wsdl.Message;
import javax.xml.soap.*;

public class SoapServiceImpl implements SoapService {

    @Override
    public String requestSoap(User user) {

        try {
            // 1. soap 메시지 생생
            MessageFactory messageFactory = MessageFactory.newInstance(); // 싱글톤?
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();

            // 2. SOAP 구조 구성
            SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
            soapEnvelope.addNamespaceDeclaration("temp", "http://inspien.co.kr/Recruit/Test");
            SOAPBody body = soapEnvelope.getBody();

            // 3. Operation 및 파라미터 추가

            SOAPElement operation = body.addChildElement("MT_RecruitingTestServices", "temp");

            SOAPElement nameElement = operation.addChildElement("NAME", "inspien");
            nameElement.addTextNode(user.getName());

            SOAPElement phoneElement = operation.addChildElement("PHONE_NUMBER", "inspien");
            phoneElement.addTextNode(user.getPhone());

            SOAPElement emailElement = operation.addChildElement("E_MAIL", "inspien");
            emailElement.addTextNode(user.getEmail());

            soapMessage.saveChanges();

            // 4. 메시지 전송
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
