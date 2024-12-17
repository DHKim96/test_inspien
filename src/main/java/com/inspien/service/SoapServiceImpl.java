package com.inspien.service;

import com.inspien.common.JDBCTemplate;
import com.inspien.model.dao.OrderDao;
import com.inspien.model.dto.*;
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
import java.sql.SQLException;
import java.util.*;

public class SoapServiceImpl implements SoapService {

    private final OrderDao orderDao;

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

        orderDao = new OrderDao();
    }

    @Override
    public SoapResponse parseSoapXML(String response) {

        SoapResponse soapResponse = null;

        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));
            is.setEncoding("UTF-8");

            // XML 파싱
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            // XML DATA 가져오기 및 Base64 디코딩
            String xmlBase64 = this.getTagValue(doc, "XML_DATA");
            String xmlDecoded = new String(Base64.getDecoder().decode(xmlBase64), "EUC-KR");

            // JSON_DATA 및 디코딩
            String jsonBase64 = getTagValue(doc, "JSON_DATA");
            String jsonDecoded = new String(Base64.getDecoder().decode(jsonBase64), "UTF-8");

            // DB_CONN_INFO 추출
            String dbconn = getTagValue(doc, "DB_CONN_INFO");

            System.out.println(dbconn);

            // DB_CONN_INFO 추출
            String ftpconn = getTagValue(doc, "FTP_CONN_INFO");

            System.out.println(ftpconn);

            soapResponse = SoapResponse.builder()
                                            .xmlData(xmlDecoded)
                                            .jsonData(jsonDecoded)
                                            .dbConnInfo(dbconn)
                                            .ftpConnInfo(ftpconn)
                                        .build();

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (Exception e){
            e.printStackTrace();
        }

        return soapResponse;
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

            soapMessage.writeTo(System.out);

            return soapMessage;
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTagValue(Document doc, String tagName) throws Exception {
        StringBuilder res = new StringBuilder();

        NodeList nodelist = doc.getElementsByTagName(tagName);

        for (int i = 0; i < nodelist.getLength(); i++){
            Node parentNode = nodelist.item(i);
            NodeList childNodes = parentNode.getChildNodes();
            if (childNodes.getLength() == 1) {
                res.append(parentNode.getTextContent());
                return res.toString();
            }
            for (int j = 0; j < childNodes.getLength(); j++){
                Node childNode = childNodes.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE){ // 요소 노드만 추출
                    res.append(childNode.getTextContent()).append(" ");
                }
            }
        }

        if (res == null){
            throw new Exception("해당 태그명의 값이 존재하지 않습니다.");
        }

        return res.toString();
    }

    @Override
    public List<OrderInsert> handleXmlDatas(String xmlData) {

        List<OrderInsert> orderInserts = new ArrayList<>();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlData));
        is.setEncoding("UTF-8");

        try {
            // XML 파싱
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            Map<Integer, OrderResponse> ordMap = parseXmlDatasHeader(doc.getElementsByTagName("HEADER"));

            Map<Integer, List<ItemResponse>> itemMap = parseXmlDatasDetail(doc.getElementsByTagName("DETAIL"));

            for (Map.Entry<Integer, List<ItemResponse>> entry : itemMap.entrySet()){
                int orderNum = entry.getKey();
                orderInserts.add(
                        OrderInsert.builder()
                                .order(ordMap.get(orderNum))
                                .items(itemMap.get(orderNum))
                                .build()
                );
            }

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        return orderInserts;
    }

    @Override
    public Map<Integer, OrderResponse> parseXmlDatasHeader(NodeList nodeList) {
        Map<Integer, OrderResponse> ordMap = new HashMap<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node parentNode = nodeList.item(i);
            NodeList childNodes = parentNode.getChildNodes();

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

        return ordMap;
    }

    @Override
    public Map<Integer, List<ItemResponse>> parseXmlDatasDetail(NodeList nodeList) {

        Map<Integer, List<ItemResponse>> itemMap = new HashMap<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node parentNode = nodeList.item(i);
            NodeList childNodes = parentNode.getChildNodes();

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
        return itemMap;
    }

    @Override
    public int insertOrderList(List<OrderInsert> orders) {

        int res = 0;

        try {
            Connection conn = JDBCTemplate.getConnection();

            for (OrderInsert orderInsert : orders) {
                for (ItemResponse itemResponse : orderInsert.getItems()) {
                    res *= orderDao.insertOrder(conn, orderInsert.getOrder(), itemResponse);
                }
            }

            conn.close();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

}
