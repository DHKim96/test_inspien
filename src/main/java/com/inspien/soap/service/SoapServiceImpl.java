package com.inspien.soap.service;

import com.inspien.common.exception.ParseCustomException;
import com.inspien.common.exception.SoapCustomException;
import com.inspien.common.util.CommonUtil;
import com.inspien.common.util.ErrCode;
import com.inspien.common.validation.UserValidator;
import com.inspien.common.validation.Validator;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import jakarta.xml.soap.*;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

/**
 * SoapService 인터페이스의 구현 클래스.
 * SOAP 메시지 생성, 요청, 응답 파싱 등을 수행합니다.
 */
@Slf4j
public class SoapServiceImpl implements SoapService {

    private final String nameSpace;
    private final String nameSpaceURI;
    private final String ENCODING = "UTF-8";

    /**
     * 기본 생성자.
     * 네임스페이스와 URI 초기화.
     */
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
                throw new SoapCustomException(ErrCode.FILE_NOT_FOUND, "hosts.properties");
            }

            prop.load(input);

            String host = prop.getProperty("soap.service.host");
            String port = prop.getProperty("soap.service.port");

            if (host == null) {
                throw new SoapCustomException(ErrCode.PROPERTY_NOT_FOUND, "soap.service.host");
            }
            
            if (port == null){
                throw new SoapCustomException(ErrCode.PROPERTY_NOT_FOUND, "soap.service.port");
            }

            endpoint = String.format("http://%s:%s/XISOAPAdapter/MessageServlet" +
                    "?senderParty=&senderService=INSPIEN&receiverParty=&receiverService=" +
                    "&interface=InspienGetRecruitingTestServicesInfo" +
                    "&interfaceNamespace=http%%3A%%2F%%2Finspien.co.kr%%2FRecruit%%2FTest", host, port);

        } catch (IOException e) {
            throw new SoapCustomException(ErrCode.FILE_NOT_READ, "hosts.properties", e);
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
                throw new SoapCustomException(ErrCode.NULL_POINT_ERROR, " SOAPMessage response");
            }
            res = getSoapResponseAsString(response);
        } catch (SOAPException e) {
            throw new SoapCustomException(ErrCode.CONNECTION_FAILED, "SOAP", e);
        }

        return res;
    }


    /**
     * SOAP 연결을 생성합니다.
     *
     * @return SOAPConnection 객체
     * @throws SoapCustomException SOAP 연결 생성 중 오류가 발생한 경우
     */
    private SOAPConnection createSoapConnection() throws SoapCustomException {
        SOAPConnection soapConnection = null;

        try {
            soapConnection = SOAPConnectionFactory.newInstance().createConnection();
        } catch (SOAPException e){
            throw new SoapCustomException(ErrCode.SOAP_NOT_CREATED, "SOAPConnection", e);
        }
        return soapConnection;
    }


    /**
     * SOAP 요청 메시지를 생성합니다.
     *
     * @param soapAction SOAP Action URL
     * @param user       요청 데이터
     * @return SOAPMessage 객체
     * @throws SoapCustomException 메시지 생성 중 오류가 발생한 경우
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
            throw new SoapCustomException(ErrCode.SOAP_NOT_CREATED, "SOAPMessage",e);
        }
        return soapMessage;
    }


    /**
     * SOAP 메시지에 Envelope와 Body를 추가합니다.
     *
     * @param soapMessage SOAP 메시지 객체
     * @param user        유저 데이터
     * @throws SoapCustomException Envelope 구성 중 오류가 발생한 경우
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
                    "E_MAIL", user.getEmail(),
                    "PHONE_NUMBER", user.getPhone(),
                    "NAME", user.getName()
                    );

            for (Map.Entry<String, String> entry : params.entrySet()) {
                operation.addChildElement(entry.getKey(), nameSpace).addTextNode(entry.getValue());
            }

        } catch (SOAPException e) {
            throw new SoapCustomException(ErrCode.SOAP_NOT_CREATED, "SOAPEnvelope", e);
        }
    }


    /**
     * SOAP 응답 데이터를 문자열로 변환합니다.
     *
     * @param response SOAPMessage 객체
     * @return 변환된 문자열
     * @throws SoapCustomException 변환 중 오류가 발생한 경우
     */
    private String getSoapResponseAsString(SOAPMessage response) throws SoapCustomException {

        String res = null;

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // SOAPMessage 를 OutputStream 에 작성
            try {
                response.writeTo(outputStream);
            } catch (IOException e) {
                throw new SoapCustomException(ErrCode.IO_STREAM_ERROR, e);
            }

            // OutputStream을 String으로 변환
            try {
                res = outputStream.toString(ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new SoapCustomException(ErrCode.SOAP_MESSAGE_TO_STRING_ERROR, e);
            }

            if (res == null || res.isEmpty()) {
                throw new SoapCustomException(ErrCode.NULL_POINT_ERROR, "SOAP_MESSAGE_TO_STRING 값");
            }

        } catch (SOAPException e) {
            throw new SoapCustomException(ErrCode.INVALID_FORMAT, "SOAPMessage", e);
        } catch (IOException e) {
            throw new SoapCustomException(ErrCode.IO_STREAM_ERROR, e);
        }

        return res;
    }


    @Override
    public SoapResponse parseSoapXML(String response) throws SoapCustomException, ParseCustomException {

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
                throw new SoapCustomException(ErrCode.SOAP_DOCUMENT_BUILDER_NOT_CREATED, e);
            }

            Document doc = null;

            try {
                doc = db.parse(is);
            } catch (SAXException e) {
                throw new SoapCustomException(ErrCode.INVALID_FORMAT, "SOAP_MESSAGE_TO_STRING", e);
            } catch (IOException e) {
                throw new SoapCustomException(ErrCode.IO_STREAM_ERROR, e);
            }


            String xmlDecoded = null;
            String jsonDecoded = null;

            try {
                // XML DATA 가져오기 및 Base64 디코딩
                String xmlBase64 = CommonUtil.getTagValue(doc, "XML_DATA");
                encoding = "EUC-KR";
                xmlDecoded = new String(Base64.getDecoder().decode(xmlBase64), encoding);

                // JSON_DATA 및 디코딩
                String jsonBase64 = CommonUtil.getTagValue(doc, "JSON_DATA");
                encoding = ENCODING;
                jsonDecoded = new String(Base64.getDecoder().decode(jsonBase64), encoding);

            } catch (UnsupportedEncodingException e) {
                throw new SoapCustomException(ErrCode.ENCODING_NOT_SUPPORTED, encoding, e);
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

        }
        catch (Exception e){
            throw new SoapCustomException(ErrCode.UNKNOWN_ERROR, e);
        }

        return soapResponse;
    }

}
