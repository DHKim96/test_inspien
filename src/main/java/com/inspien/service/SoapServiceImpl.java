package com.inspien.service;

import com.inspien.model.dto.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Base64;
import java.util.Properties;

public class SoapServiceImpl implements SoapService {

    @Override
    public String requestSoap(User user) {

        try {
            Properties prop = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("hosts.properties");
            prop.load(input);

            String inspienpoc = prop.getProperty("soap.service.host");
            String port = prop.getProperty("soap.service.port");
            String endPoint = "http://" + inspienpoc + ":" + port + "/XISOAPAdapter/MessageServlet?senderParty=&senderService=INSPIEN&receiverParty=&receiverService=&interface=InspienGetRecruitingTestServicesInfo&interfaceNamespace=http%3A%2F%2Finspien.co.kr%2FRecruit%2FTest";

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

            SOAPElement nameElement = operation.addChildElement("NAME", "temp");
            nameElement.addTextNode(user.getName());

            SOAPElement phoneElement = operation.addChildElement("PHONE_NUMBER", "temp");
            phoneElement.addTextNode(user.getPhone());

            SOAPElement emailElement = operation.addChildElement("E_MAIL", "temp");
            emailElement.addTextNode(user.getEmail());

            soapMessage.saveChanges();

            // 4. SOAP request
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            System.out.println("Request SOAP service at :" + endPoint);

            SOAPMessage response = soapConnection.call(soapMessage, endPoint);
            
            // 5. SOAP response 처리
            System.out.println("Response SOAP Message : ");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.writeTo(outputStream); // SOAPBody 전체를 출력
            String res = outputStream.toString("UTF-8");
            soapConnection.close();
            return res;       // 문자열로 변환
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SOAP 요청 실패: " + e.getMessage());
        }
    }

    @Override
    public void parseSoapXML(String soapXmlData) {


        try {

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(soapXmlData));
            is.setEncoding("UTF-8");
            
            // XML 파싱
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);


//            // XML DATA 가져오기 및 Base64 디코딩
//            String xmlBase64 = this.getTagValue(doc, "XML_DATA");
//            String xmlDecoded = new String(Base64.getDecoder().decode(xmlBase64), "EUC-KR");
//            System.out.println("Decoded XML_DATA: \n" + xmlDecoded);
//
//            // JSON_DATA 및 디코딩
//            String jsonBase64 = getTagValue(doc, "JSON_DATA");
//            String jsonDecoded = new String(Base64.getDecoder().decode(jsonBase64), "UTF-8");
//            System.out.println("Decoded JSON_DATA: \n" + jsonDecoded);

            // DB_CONN_INFO 추출
            String dbconn = getTagValue(doc, "DB_CONN_INFO");
            System.out.println("DB Connection Info =" + dbconn);

            // DB_CONN_INFO 추출
            String ftpconn = getTagValue(doc, "FTP_CONN_INFO");
            System.out.println("FTP_CONN_INFO =" + ftpconn);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTagValue(Document doc, String tagName) {
        NodeList nodelist = doc.getElementsByTagName(tagName);

        StringBuilder res = null;

        System.out.println(nodelist.getLength());

        Element e = null;

        for(int i=0; i< nodelist.getLength(); i++) {
            e = (Element)nodelist.item(i);
            res.append(e.getTextContent());
        }

        return res.toString();
    }

}
