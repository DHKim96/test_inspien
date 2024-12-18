package com.inspien.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspien.exception.*;
import com.inspien.util.FtpClientUtil;
import com.inspien.util.JDBCTemplate;
import com.inspien.model.dao.OrderDao;
import com.inspien.model.dto.*;
import com.inspien.model.dto.Record;
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
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class SoapServiceImpl implements SoapService {

    private final OrderDao orderDao;

    private final String endPoint;
    private final String nameSpace;
    private final String nameSpaceURI;
    private static final String ENCODING = "UTF-8";

    public SoapServiceImpl() {
        nameSpace = "in";
        nameSpaceURI = "http://inspien.co.kr/Recruit/Test";
        endPoint = this.loadEndPoint();
        orderDao = new OrderDao();
    }

    // ===================================== SOAP 호출 =====================================


    /**
     * SOAP 호출 및 Response를 String 으로 변환
     * @param user : 유저 정보
     * @return SOAPMessage 를 String 으로 형 변환한 값
     */
    @Override
    public String requestSoapWebService(User user) throws SoapServiceException {
        String res = null;
        try (SOAPConnection soapConnection = this.createSoapConnection()) {
            SOAPMessage request = this.createSoapRequest("http://sap.com/xi/WebService/soap1.1", user);
            SOAPMessage response = soapConnection.call(request, endPoint);
            if (response == null) {
                throw new SoapServiceException("응답 받은 SOAPMessage 가 NULL 입니다.");
            }
            res = getSoapResponseAsString(response);
        } catch (SOAPException e) {
            throw new SoapServiceException("SOAP 호출 실패", e);
        }

        return res;
    }

    /**
     * SOAP 커넥션 생성하는 메서드
     * @return
     * @throws SOAPException
     */
    @Override
    public SOAPConnection createSoapConnection() throws SoapServiceException {
        SOAPConnection soapConnection = null;

        try {
            soapConnection = SOAPConnectionFactory.newInstance().createConnection();
        } catch (SOAPException e){
            throw new SoapServiceException("SOAP 커넥션 생성에 실패했습니다.", e);
        }
        return soapConnection;
    }

    /**
     * Response 인 SOAP Message 를 String 으로 변환하는 메서드
     * @param response : 응답받은 SOAPMessage
     * @return : 응답받은 SOAPMessage 를 String 으로 변환
     * @throws IOException
     * @throws SOAPException
     */
    @Override
    public String getSoapResponseAsString(SOAPMessage response) throws SoapServiceException {

        String res = null;

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // SOAPMessage 를 OutputStream 에 작성
            response.writeTo(outputStream);

            // OutputStream을 String으로 변환
            res = outputStream.toString(ENCODING);

            if (res == null || res.isEmpty()) {
                throw new SoapServiceException("SOAP 응답 Message 변환 값이 존재하지 않습니다.");
            }

        } catch (SOAPException e) {
            throw new SoapServiceException("SOAPMessage의 형식이 적절하지 않습니다.", e);
        } catch (IOException e) {
            throw new SoapServiceException("SOAPMessage 직렬화 중 입출력 오류가 발생했습니다.", e);
        }

        return res;
    }

    /**
     * SOAP 메시지의 Envelope, body 구성하는 메서드
     * @param soapMessage : 생성한 SOAP 메시지
     * @param user : 유저 정보
     */
    @Override
    public void createSoapEnvelope(SOAPMessage soapMessage, User user) throws SoapServiceException {
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
            throw new SoapServiceException("SOAP Envelope 생성에 실패했습니다.", e);
        }
    }

    /**
     * SOAP Response 데이터에서 XML_DATA, JSON_DATA, DB연결정보, FTP연결정보 추출하는 메서드
     * @param response : SOAP Response
     * @return soapResponse : 각 정보들을 담은 객체
     */
    @Override
    public SoapResponse parseSoapXML(String response) throws SoapServiceException{

        SoapResponse soapResponse = null;

        String encoding = null;

        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));
            is.setEncoding(ENCODING);

            // XML 파싱
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            // XML DATA 가져오기 및 Base64 디코딩
            String xmlBase64 = this.getTagValue(doc, "XML_DATA");
            encoding = "EUC-KR";
            String xmlDecoded = new String(Base64.getDecoder().decode(xmlBase64), encoding);

            // JSON_DATA 및 디코딩
            String jsonBase64 = getTagValue(doc, "JSON_DATA");
            encoding = ENCODING;
            String jsonDecoded = new String(Base64.getDecoder().decode(jsonBase64), encoding);

            // DB_CONN_INFO 추출
            String dbconn = getTagValue(doc, "DB_CONN_INFO");

            // DB_CONN_INFO 추출
            String ftpconn = getTagValue(doc, "FTP_CONN_INFO");

            soapResponse = SoapResponse.builder()
                    .xmlData(xmlDecoded)
                    .jsonData(jsonDecoded)
                    .dbConnInfo(dbconn)
                    .ftpConnInfo(ftpconn)
                    .build();

        } catch (UnsupportedEncodingException e) {
            throw new SoapServiceException(encoding + "은(는) 지원하지 않는 인코딩 형식입니다.", e);
        } catch (ParserConfigurationException e) {
            throw new SoapServiceException("DocumentBuilder 생성에 실패했습니다.",e);
        } catch (IOException e) {
            throw new SoapServiceException("DocumentBuilder 파싱 시 입출력 오류가 발생했습니다.",e);
        } catch (SAXException e) {
            throw new SoapServiceException("response 가 유효하지 않은 형식입니다.", e);
        }

        return soapResponse;
    }

    /**
     * SOAP 메시지, HEADER 생성하는 메서드
     * @param soapAction : wsdl 내 soapAction 값
     * @param user
     * @return
     */
    @Override
    public SOAPMessage createSoapRequest(String soapAction, User user) throws SoapServiceException {
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
            throw new SoapServiceException("SOAP 메시지 생성에 실패했습니다.",e);
        }

        return soapMessage;
    }

    /**
     * SOAP Response 의 각 태그들 내 요소를 추출하는 메서드
     * @param doc
     * @param tagName : 태그명
     * @return 각 태그별 요소의 TEXT
     * @throws Exception
     */
    public String getTagValue(Document doc, String tagName) throws SoapServiceException {
        StringBuilder res = new StringBuilder();

        NodeList nodelist = doc.getElementsByTagName(tagName);

        if (nodelist.getLength() == 0) {
            throw new SoapServiceException("WSDL 에서 [%s] 태그가 존재하지 않습니다.".formatted(tagName));
        }

        for (int i = 0; i < nodelist.getLength(); i++) {
            Node parentNode = nodelist.item(i);
            NodeList childNodes = parentNode.getChildNodes();
            if (childNodes.getLength() == 1) {
                res.append(parentNode.getTextContent());
                return res.toString();
            }
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) { // 요소 노드만 추출
                    res.append(childNode.getTextContent()).append(" ");
                }
            }
        }

        if (res.isEmpty()) {
            throw new SoapServiceException("[%s] 태그 내에 값이 존재하지 않습니다.".formatted(tagName));
        }

        return res.toString();
    }

    // ===================================== XML_DATA 처리 =====================================

    @Override
    public List<OrderInsert> handleXmlDatas(String xmlData) throws XmlProcessingException {

        List<OrderInsert> orderInserts = new ArrayList<>();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlData));
        is.setEncoding(ENCODING);

        try {
            // XML 파싱
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            NodeList headerNodeList = doc.getElementsByTagName("HEADER");
            if (headerNodeList.getLength() == 0) {
                throw new XmlProcessingException("xmlData 에 HEADER 태그가 존재하지 않습니다.");
            }
            Map<Integer, OrderResponse> ordMap = parseXmlDatasHeader(headerNodeList);

            NodeList detailNodeList = doc.getElementsByTagName("DETAIL");
            if (detailNodeList.getLength() == 0) {
                throw new XmlProcessingException("xmlData 에 DETAIL 태그가 존재하지 않습니다.");
            }
            Map<Integer, List<ItemResponse>> itemMap = parseXmlDatasDetail(detailNodeList);

            for (Map.Entry<Integer, List<ItemResponse>> entry : itemMap.entrySet()) {
                int orderNum = entry.getKey();
                orderInserts.add(
                        OrderInsert.builder()
                                .order(ordMap.get(orderNum))
                                .items(itemMap.get(orderNum))
                                .build()
                );
            }

        } catch (ParserConfigurationException e) {
            throw new XmlProcessingException("DocumentBuilder 생성에 실패했습니다.",e);
        } catch (IOException e) {
            throw new XmlProcessingException("DocumentBuilder 파싱 시 입출력 오류가 발생했습니다.",e);
        } catch (SAXException e) {
            throw new XmlProcessingException("xmlData 가 유효하지 않은 형식입니다.", e);
        }

        return orderInserts;
    }

    @Override
    public Map<Integer, OrderResponse> parseXmlDatasHeader(NodeList nodeList) throws XmlProcessingException{

        Map<Integer, OrderResponse> ordMap = new HashMap<>();

        int errorIndex = 0;

        try {

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node parentNode = nodeList.item(i);

                errorIndex = i;

                if (parentNode == null) {
                    throw new XmlProcessingException("HEADER 에 해당 ParentNode 가 존재하지 않습니다.");
                }

                NodeList childNodes = parentNode.getChildNodes();

                if (childNodes.getLength() < 10) {
                    throw new XmlProcessingException("HEADER 노드의 자식 노드가 예상보다 적습니다. 노드 길이: " + (childNodes != null ? childNodes.getLength() : 0));
                }

                int orderNum = Integer.parseInt(childNodes.item(0).getTextContent());
                String orderId = childNodes.item(1).getTextContent();
                String orderDate = childNodes.item(2).getTextContent();
                int orderPrice = Integer.parseInt(childNodes.item(3).getTextContent());
                int orderQty = Integer.parseInt(childNodes.item(4).getTextContent());
                String receiverName = childNodes.item(5).getTextContent();
                String receiverNo = childNodes.item(6).getTextContent();
                String etaDate = childNodes.item(7).getTextContent();
                String destination = childNodes.item(8).getTextContent();
                String description = childNodes.item(9).getTextContent();

                OrderResponse orderResponse = OrderResponse.builder()
                        .orderNum(orderNum)
                        .orderId(orderId)
                        .orderDate(orderDate)
                        .orderPrice(orderPrice)
                        .orderQty(orderQty)
                        .receiverName(receiverName)
                        .receiverNo(receiverNo)
                        .etaDate(etaDate)
                        .destination(destination)
                        .description(description)
                        .build();

                ordMap.put(orderNum, orderResponse);
            }
        } catch (NumberFormatException e) {
            throw new XmlProcessingException("XML_DATA HEADER 파싱 중 숫자 형식 파싱 오류가 발생했습니다. 노드 인덱스: " + errorIndex, e);
        } catch (NullPointerException e) {
            throw new XmlProcessingException("XML_DATA HEADER 필수 노드 또는 값이 null입니다. 노드 인덱스: " + errorIndex, e);
        } catch (Exception e) {
            throw new XmlProcessingException("XML_DATA HEADER 파싱 중 알 수 없는 예외가 발생했습니다. 노드 인덱스: " + errorIndex, e);
        }

        return ordMap;
    }

    @Override
    public Map<Integer, List<ItemResponse>> parseXmlDatasDetail(NodeList nodeList) throws XmlProcessingException{

        Map<Integer, List<ItemResponse>> itemMap = new HashMap<>();

        int errorIndex = 0;

        try {

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node parentNode = nodeList.item(i);

                errorIndex = i;

                if (parentNode == null) {
                    throw new XmlProcessingException("DETAIL 에 해당 ParentNode 가 존재하지 않습니다.");
                }

                NodeList childNodes = parentNode.getChildNodes();

                if (childNodes.getLength() < 6) {
                    throw new XmlProcessingException("DETAIL 노드의 자식 노드가 예상보다 적습니다. 노드 길이: " + (childNodes != null ? childNodes.getLength() : 0));
                }

                int orderNum = Integer.parseInt(childNodes.item(0).getTextContent());
                int itemSeq = Integer.parseInt(childNodes.item(1).getTextContent());
                String itemName = childNodes.item(2).getTextContent();
                int itemQty = Integer.parseInt(childNodes.item(3).getTextContent());
                String itemColor = childNodes.item(4).getTextContent();
                int itemPrice = Integer.parseInt(childNodes.item(5).getTextContent());

                ItemResponse item = ItemResponse.builder()
                        .orderNum(orderNum)
                        .itemSeq(itemSeq)
                        .itemName(itemName)
                        .itemQty(itemQty)
                        .itemColor(itemColor)
                        .itemPrice(itemPrice)
                        .build();


                if (!itemMap.containsKey(orderNum)) {
                    itemMap.put(orderNum, new ArrayList<>());
                }

                itemMap.get(orderNum).add(item);
            }

        } catch (NumberFormatException e) {
            throw new XmlProcessingException("XML_DATA DETAIL 파싱 중 숫자 형식 파싱 오류가 발생했습니다. 노드 인덱스: " + errorIndex, e);
        } catch (NullPointerException e) {
            throw new XmlProcessingException("XML_DATA DETAIL 필수 노드 또는 값이 null입니다. 노드 인덱스: " + errorIndex, e);
        } catch (Exception e) {
            throw new XmlProcessingException("XML_DATA DETAIL 파싱 중 알 수 없는 예외가 발생했습니다. 노드 인덱스: " + errorIndex, e);
        }
        
        return itemMap;
    }

    @Override
    public int insertOrderList(List<OrderInsert> orders, Map<String, String> dbconfig) throws DataBaseException {

        int res = 1;

        Connection conn = null;

        conn = JDBCTemplate.getConnection(dbconfig.get("host"), dbconfig.get("port"), dbconfig.get("sid"), dbconfig.get("user"), dbconfig.get("password"));

        for (OrderInsert orderInsert : orders) {
            for (ItemResponse itemResponse : orderInsert.getItems()) {
                res *= orderDao.insertOrder(conn, orderInsert.getOrder(), itemResponse);
                if (res == 0){
                    throw new DataBaseException("ORDERS 데이터 INSERT에 실패했습니다.");
                }
            }
        }

        JDBCTemplate.close(conn);

        return res;
    }

    // ===================================== JSON_DATA 처리 =====================================

    @Override
    public <T> List<T> jsonDataToList(String jsonData, String tagName) throws InsJsonProcessingException {
        List<T> res = null;

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 해당 필드를 가져와 List<해당 필드> 로 변환
            JsonNode rootNode = objectMapper.readTree(jsonData);

            JsonNode chileNode = rootNode.get(tagName);

            if (chileNode == null || !chileNode.isArray()) {
                throw new InsJsonProcessingException(String.format("JSON 데이터에서 [%s] 필드가 존재하지 않거나 배열 형태가 아닙니다.", tagName));
            }

            res = objectMapper.readValue(chileNode.toString(), new TypeReference<ArrayList<T>>() {});
        } catch (JsonProcessingException e) {
            throw new InsJsonProcessingException(String.format("JSON 데이터를 %s 인스턴스로 역직렬화 중 JSON 매핑에 실패했습니다.", tagName), e);
        }

        return res;
    }

    @Override
    public void convertToFlatFile(List<Record> records, String localUploadPath, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(localUploadPath + fileName))) {
            for (Record record : records) {
                // 각 필드를 |로 구분해 한 줄로 작성
                String flatLine = String.join("^",
                        record.getNames(),
                        record.getPhone(),
                        record.getEmail(),
                        record.getBirthday(),
                        record.getCompany(),
                        record.getPersonalNumber(),
                        record.getOrganizationNumber(),
                        record.getCountry(),
                        record.getRegion(),
                        record.getCity(),
                        record.getStreet(),
                        record.getZipCode(),
                        record.getCreditCard(),
                        record.getGuid()
                ) + "\\n";

                writer.write(flatLine);
                writer.newLine(); // 다음 줄로 이동
            }
        } catch (IOException e) {
            throw new FtpClientException("jsonData to Flat file 생성 실패 에러 메시지 : " + e.getMessage(), e);
        }
    }

    @Override
    public String createSaveFileName(String name) {
        String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        return String.format("INSPIEN_JSON_[%s]_[%s].txt", name, currentTime);
    }

    @Override
    public boolean uploadFileToFtp(Map<String, String> ftpConfig, String localFilePath, String fileName) throws FtpClientException {
        boolean isUploaded = false;
        FtpClientUtil ftpClientUtil = null;

        try {
            // 입력값 검증
            if (ftpConfig == null || ftpConfig.isEmpty()) {
                throw new FtpClientException("FTP 연결 정보가 비어있습니다.");
            }

            if (localFilePath == null || localFilePath.isEmpty()) {
                throw new FtpClientException("로컬 파일 저장 경로가 비어있습니다.");
            }

            if (fileName == null || fileName.isEmpty()) {
                throw new FtpClientException("파일명이 존재하지 않습니다.");
            }
            
            // FTP 연결 정보 추출
            String host = ftpConfig.get("host");
            String portString = ftpConfig.get("port");
            String user = ftpConfig.get("user");
            String password = ftpConfig.get("password");
            String filepath = ftpConfig.get("filepath");

            if (host == null || portString == null || user == null || password == null || filepath == null) {
                throw new FtpClientException("FTP 설정 정보 중 필수 값이 누락되었습니다.");
            }

            int port;

            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                throw new FtpClientException("포트 정보가 숫자 형식이 아닙니다: " + portString, e);
            }

            String localUploadPath = localFilePath + fileName;
            String remoteUploadPath = filepath + fileName;

            ftpClientUtil = new FtpClientUtil(host, port, user, password);

            isUploaded = ftpClientUtil.uploadFile(localUploadPath, remoteUploadPath);

            if (!isUploaded) {
                throw new FtpClientException("파일 업로드에 실패했습니다. 경로: " + remoteUploadPath);
            }

        } catch (Exception e){
            throw new FtpClientException("알 수 없는 오류가 발생했습니다. 에러 메시지 : " + e.getMessage(), e);
        } finally {
            ftpClientUtil.disconnect();
        }


        return isUploaded;
    }

    // ===================================== 유틸 메서드 ==========================================

    @Override
    public String loadEndPoint() {
        Properties prop = new Properties();
        String endpoint;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("hosts.properties")) {
            if (input == null) {
                throw new RuntimeException("hosts.properties 파일이 클래스패스에 존재하지 않습니다.");
            }

            prop.load(input);

            String host = prop.getProperty("soap.service.host");
            String port = prop.getProperty("soap.service.port");

            if (host == null || port == null) {
                throw new RuntimeException("필수 프로퍼티 (soap.service.host 또는 soap.service.port)가 설정되지 않았습니다.");
            }

            endpoint = String.format("http://%s:%s/XISOAPAdapter/MessageServlet" +
                    "?senderParty=&senderService=INSPIEN&receiverParty=&receiverService=" +
                    "&interface=InspienGetRecruitingTestServicesInfo" +
                    "&interfaceNamespace=http%%3A%%2F%%2Finspien.co.kr%%2FRecruit%%2FTest", host, port);

        } catch (IOException e) {
            throw new RuntimeException("hosts.properties 파일을 읽는 중 오류 발생: " + e.getMessage(), e);
        }

        return endpoint;
    }

    @Override
    public Map<String, String> connInfoToMap(String connInfo, int type) throws XmlProcessingException, IllegalArgumentException {
        Map<String, String> connInfoMap = new HashMap<>();

        String[] dbConnInfoKeys = {"host", "port", "sid", "user", "password", "tablename"};
        String[] ftpConnInfoKeys = {"host", "port", "user", "password", "filepath"};

        String[] keys = type == 1 ? dbConnInfoKeys : ftpConnInfoKeys;

        // 데이터 유효성 검사
        if (connInfo == null || connInfo.trim().isEmpty()) {
            throw new XmlProcessingException("Connection Info 가 비어있습니다.");
        }

        // 공백 기준으로 문자열 분할
        String[] tokens = connInfo.split(" ");

        if (tokens.length != keys.length) {
            throw new IllegalArgumentException("Connection Info의 형식이 잘못되었습니다. 필요한 데이터 개수: " + keys.length);
        }

        // 키와 값을 매칭하여 Map에 추가
        for (int i = 0; i < keys.length; i++) {
            connInfoMap.put(keys[i], tokens[i]);
        }

        return connInfoMap;
    }

}
