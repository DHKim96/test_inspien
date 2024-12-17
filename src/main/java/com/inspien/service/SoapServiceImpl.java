package com.inspien.service;

import com.inspien.model.dto.User;
import jakarta.xml.soap.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;

public class SoapServiceImpl implements SoapService {

    private final String endPoint;
    private final String nameSpace = "in";
    private final String nameSpaceURI = "http://inspien.co.kr/Recruit/Test";

    public SoapServiceImpl() {
        Properties prop = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream("hosts.properties");
        try {
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException("hosts.properties 를 읽어올 수 없습니다.");
        }
        String inspienpoc = prop.getProperty("soap.service.host");
        String port = prop.getProperty("soap.service.port");
        endPoint = "http://" + inspienpoc + ":" + port + "/XISOAPAdapter/MessageServlet?senderParty=&senderService=INSPIEN&receiverParty=&receiverService=&interface=InspienGetRecruitingTestServicesInfo&interfaceNamespace=http%3A%2F%2Finspien.co.kr%2FRecruit%2FTest";
    }

    @Override
    public String requestSoap(User user) {

        try {

            URL url = new URL(endPoint);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true); // 안해줬더니 에러 발생

            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "text/xml");

            String sendMessage = """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                                    xmlns:ins="http://inspien.co.kr/Recruit/Test">
                        <soap:Body>
                            <ins:MT_RecruitingTestServices>
                                <ins:NAME>%s</ins:NAME>
                                <ins:PHONE_NUMBER>%s</ins:PHONE_NUMBER>
                                <ins:E_MAIL>%s</ins:E_MAIL>
                            </ins:MT_RecruitingTestServices>
                        </soap:Body>
                    </soap:Envelope>
                    """.formatted(user.getName(), user.getPhone(), user.getEmail());

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(sendMessage);
            wr.flush();

            // 결과 읽기
            String inputLine = null;
            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }


            System.out.println("response :" + response);

            in.close();
            wr.close();
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException("SOAP 요청 실패: " + e.getMessage());
        }
    }

    @Override
    public void parseSoapXML(String response) {


        try {

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));
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

    @Override
    public void createSoapEnvelope(SOAPMessage soapMessage, User user) {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        try {
            // SOAP Envelope 생성
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration(nameSpace, nameSpaceURI);
            
            // SOAP BODY 생성
            SOAPBody body = envelope.getBody();
            SOAPElement operation = body.addChildElement("MT_RecruitingTestServices", "in");

            // 파라미터 추가
            operation.addChildElement("NAME", "in").addTextNode(user.getName());
            operation.addChildElement("PHONE_NUMBER", "in").addTextNode(user.getPhone());
            operation.addChildElement("E_MAIL", "in").addTextNode(user.getEmail());

        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String requestSoapWebService(User user) {
        try {
            
            // SOAP 커넥션 생성
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = factory.createConnection();
            
            // SOAP 메시지 전송

            SOAPMessage request = this.createSoapRequest("http://sap.com/xi/WebService/soap1.1", user);
            SOAPMessage response = soapConnection.call(request, endPoint);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.writeTo(outputStream);
            String res = outputStream.toString("UTF-8");       // 문자열로 변환

            soapConnection.close();
            outputStream.close();

            return res;
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public SOAPMessage createSoapRequest(String soapAction, User user) {

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();

            SOAPMessage soapMessage = messageFactory.createMessage();
            
            //  BODY 생성
            this.createSoapEnvelope(soapMessage, user);

            // Header 생성(soapAction 존재)
            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", soapAction);

            soapMessage.saveChanges();

            System.out.println("Request SOAP Message:");
            soapMessage.writeTo(System.out);
            System.out.println("\n");

            return soapMessage;
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTagValue(Document doc, String tagName) {
        NodeList nodelist = doc.getElementsByTagName(tagName);

        StringBuilder res = null;

        System.out.println(nodelist.getLength());

        Element e = null;

        for (int i = 0; i < nodelist.getLength(); i++) {
            e = (Element) nodelist.item(i);
            res.append(e.getTextContent());
        }

        return res.toString();
    }

}
