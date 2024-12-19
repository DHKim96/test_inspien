package com.inspien.soap.service;

import com.inspien.common.exception.SoapCustomException;
import com.inspien.common.util.CommonUtil;
import com.inspien.common.validation.UserValidator;
import com.inspien.common.validation.Validator;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import jakarta.xml.soap.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

public class SoapServiceImpl implements SoapService {

    private final String nameSpace;
    private final String nameSpaceURI;
    private final String ENCODING = "UTF-8";

    public SoapServiceImpl() {
        nameSpaceURI = "http://inspien.co.kr/Recruit/Test";
        nameSpace = "in";
    }

    @Override
    public String loadEndPoint() throws SoapCustomException{
        Properties prop = new Properties();
        String endpoint;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("hosts.properties")) {
            if (input == null) {
                throw new SoapCustomException("hosts.properties 파일이 클래스패스에 존재하지 않습니다.");
            }

            prop.load(input);

            String host = prop.getProperty("soap.service.host");
            String port = prop.getProperty("soap.service.port");

            if (host == null) {
                throw new SoapCustomException("host 프로퍼티가 설정되지 않았습니다.");
            }
            
            if (port == null){
                throw new SoapCustomException("port 프로퍼티가 설정되지 않았습니다.");
            }

            endpoint = String.format("http://%s:%s/XISOAPAdapter/MessageServlet" +
                    "?senderParty=&senderService=INSPIEN&receiverParty=&receiverService=" +
                    "&interface=InspienGetRecruitingTestServicesInfo" +
                    "&interfaceNamespace=http%%3A%%2F%%2Finspien.co.kr%%2FRecruit%%2FTest", host, port);

        } catch (IOException e) {
            throw new SoapCustomException("hosts 파일을 읽는 중 오류가 발생했습니다.: " + e.getMessage(), e);
        }

        return endpoint;
    }

    /**
     * SOAP 호출 및 Response를 String 으로 변환
     * @param user : 유저 정보
     * @return SOAPMessage 를 String 으로 형 변환한 값
     */
    @Override
    public String requestSoapWebService(User user, String endPoint) throws SoapCustomException {
        Validator<User> validator = new UserValidator();
        // 유효성 검증
        validator.validate(user);

        String res = null;
        try (SOAPConnection soapConnection = this.createSoapConnection()) {
            SOAPMessage request = this.createSoapRequest("http://sap.com/xi/WebService/soap1.1", user);
            SOAPMessage response = soapConnection.call(request, endPoint);
            if (response == null) {
                throw new SoapCustomException("응답 받은 SOAPMessage 가 NULL 입니다.");
            }
            res = getSoapResponseAsString(response);
        } catch (SOAPException e) {
            throw new SoapCustomException("SOAP 호출 실패", e);
        }

        return res;
    }

    /**
     * SOAP 커넥션 생성하는 메서드
     * @return
     * @throws SOAPException
     */
    private SOAPConnection createSoapConnection() throws SoapCustomException {
        SOAPConnection soapConnection = null;

        try {
            soapConnection = SOAPConnectionFactory.newInstance().createConnection();
        } catch (SOAPException e){
            throw new SoapCustomException("SOAP 커넥션 생성에 실패했습니다.", e);
        }
        return soapConnection;
    }

    /**
     * SOAP 메시지, HEADER 생성하는 메서드
     * @param soapAction : wsdl 내 soapAction 값
     * @param user
     * @return
     */
    private SOAPMessage createSoapRequest(String soapAction, User user) throws SoapCustomException {
        SOAPMessage soapMessage = null;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();

            soapMessage = messageFactory.createMessage();

            //  BODY 생성
            this.createSoapEnvelope(soapMessage, user);

            // Header 생성(soapAction 존재)
            MimeHeaders headers = soapMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", soapAction);

            soapMessage.saveChanges();
        } catch (SOAPException e) {
            throw new SoapCustomException("SOAP 메시지 생성에 실패했습니다.",e);
        }

        return soapMessage;
    }

    /**
     * SOAP 메시지의 Envelope, body 구성하는 메서드
     * @param soapMessage : 생성한 SOAP 메시지
     * @param user : 유저 정보
     */
    private void createSoapEnvelope(SOAPMessage soapMessage, User user) throws SoapCustomException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        try {
            // SOAP Envelope 생성
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration(nameSpace, nameSpaceURI);

            // SOAP BODY 생성
            SOAPBody body = envelope.getBody();
            SOAPElement operation = body.addChildElement("MT_RecruitingTestServices", "in");

            // 파라미터 추가
            Map<String, String> params = Map.of(
                    "NAME", user.getName(),
                    "PHONE_NUMBER", user.getPhone(),
                    "E_MAIL", user.getEmail()
            );

            for (Map.Entry<String, String> entry : params.entrySet()) {
                operation.addChildElement(entry.getKey(), nameSpace).addTextNode(entry.getValue());
            }

        } catch (SOAPException e) {
            throw new SoapCustomException("SOAP Envelope 생성에 실패했습니다.", e);
        }
    }

    /**
     * Response 인 SOAP Message 를 String 으로 변환하는 메서드
     * @param response : 응답받은 SOAPMessage
     * @return : 응답받은 SOAPMessage 를 String 으로 변환
     */
    private String getSoapResponseAsString(SOAPMessage response) throws SoapCustomException {

        String res = null;

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // SOAPMessage 를 OutputStream 에 작성
            try {
                response.writeTo(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // OutputStream을 String으로 변환
            try {
                res = outputStream.toString(ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            if (res == null || res.isEmpty()) {
                throw new SoapCustomException("SOAP 응답 Message 변환 값이 존재하지 않습니다.");
            }

        } catch (SOAPException e) {
            throw new SoapCustomException("SOAPMessage의 형식이 적절하지 않습니다.", e);
        } catch (IOException e) {
            throw new SoapCustomException("OutputStream 생성에 실패했습니다", e);
        }

        return res;
    }

    /**
     * SOAP Response 데이터에서 XML_DATA, JSON_DATA, DB연결정보, FTP연결정보 추출하는 메서드
     * @param response : SOAP Response
     * @return soapResponse : 각 정보들을 담은 객체
     */
    @Override
    public SoapResponse parseSoapXML(String response) throws SoapCustomException {

        SoapResponse soapResponse = null;

        String encoding = null;

        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));
            is.setEncoding(ENCODING);

            // XML 파싱
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new SoapCustomException("DocumentBuilder 생성에 실패했습니다.", e);
            }

            Document doc = null;

            try {
                doc = db.parse(is);
            } catch (SAXException e) {
                throw new SoapCustomException("response 가 유효하지 않은 형식입니다.", e);
            } catch (IOException e) {
                throw new SoapCustomException("DocumentBuilder 파싱 시 입출력 오류가 발생했습니다.", e);
            }

            // XML DATA 가져오기 및 Base64 디코딩
            String xmlBase64 = CommonUtil.getTagValue(doc, "XML_DATA");
            encoding = "EUC-KR";
            String xmlDecoded = null;

            // JSON_DATA 및 디코딩
            String jsonBase64 = CommonUtil.getTagValue(doc, "JSON_DATA");
            encoding = ENCODING;
            String jsonDecoded = null;

            try {
                xmlDecoded = new String(Base64.getDecoder().decode(xmlBase64), encoding);
                jsonDecoded = new String(Base64.getDecoder().decode(jsonBase64), encoding);
            } catch (UnsupportedEncodingException e) {
                throw new SoapCustomException(encoding + "은(는) 지원하지 않는 인코딩 형식입니다.", e);
            }

            // DB_CONN_INFO 추출
            String dbconn = CommonUtil.getTagValue(doc, "DB_CONN_INFO");

            // DB_CONN_INFO 추출
            String ftpconn = CommonUtil.getTagValue(doc, "FTP_CONN_INFO");

            soapResponse = SoapResponse.builder()
                    .xmlData(xmlDecoded)
                    .jsonData(jsonDecoded)
                    .dbConnInfo(dbconn)
                    .ftpConnInfo(ftpconn)
                    .build();

        } catch (Exception e){
            throw new SoapCustomException("알 수 없는 오류가 발생했습니다. 에러 메시지 : " + e.getMessage() , e);
        }

        return soapResponse;
    }

}
