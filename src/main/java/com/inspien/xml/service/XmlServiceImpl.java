package com.inspien.xml.service;

import com.inspien.common.exception.DbCustomException;
import com.inspien.common.exception.XmlCustomException;
import com.inspien.common.util.JDBCTemplate;
import com.inspien.xml.dao.OrderDao;
import com.inspien.xml.dto.ItemResponse;
import com.inspien.xml.dto.OrderInsert;
import com.inspien.xml.dto.OrderResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlServiceImpl implements XmlService {

    private final String ENCODING;

    private final OrderDao orderDao;

    public XmlServiceImpl() {
        ENCODING = "UTF-8";
        orderDao = new OrderDao();
    }

    @Override
    public List<OrderInsert> handleXmlData(String xmlData) throws XmlCustomException {

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
                throw new XmlCustomException("xmlData 에 HEADER 태그가 존재하지 않습니다.");
            }
            Map<Integer, OrderResponse> ordMap = parseXmlDataHeader(headerNodeList);

            NodeList detailNodeList = doc.getElementsByTagName("DETAIL");
            if (detailNodeList.getLength() == 0) {
                throw new XmlCustomException("xmlData 에 DETAIL 태그가 존재하지 않습니다.");
            }
            Map<Integer, List<ItemResponse>> itemMap = parseXmlDataDetail(detailNodeList);

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
            throw new XmlCustomException("DocumentBuilder 생성에 실패했습니다.",e);
        } catch (IOException e) {
            throw new XmlCustomException("DocumentBuilder 파싱 시 입출력 오류가 발생했습니다.",e);
        } catch (SAXException e) {
            throw new XmlCustomException("xmlData 가 유효하지 않은 형식입니다.", e);
        }

        return orderInserts;
    }

    private Map<Integer, OrderResponse> parseXmlDataHeader(NodeList nodeList) throws XmlCustomException {

        Map<Integer, OrderResponse> ordMap = new HashMap<>();

        int errorIndex = 0;

        try {

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node parentNode = nodeList.item(i);

                errorIndex = i;

                if (parentNode == null) {
                    throw new XmlCustomException("HEADER 에 해당 ParentNode 가 존재하지 않습니다.");
                }

                NodeList childNodes = parentNode.getChildNodes();

                if (childNodes.getLength() < 10) {
                    throw new XmlCustomException("HEADER 노드의 자식 노드가 예상보다 적습니다. 노드 길이: " + (childNodes != null ? childNodes.getLength() : 0));
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
        } catch (NullPointerException e) {
            throw new XmlCustomException("XML_DATA HEADER 필수 노드 또는 값이 null입니다. 노드 인덱스: " + errorIndex, e);
        } catch (Exception e) {
            throw new XmlCustomException("XML_DATA HEADER 파싱 중 알 수 없는 예외가 발생했습니다. 노드 인덱스: " + errorIndex, e);
        }

        return ordMap;
    }

    private Map<Integer, List<ItemResponse>> parseXmlDataDetail(NodeList nodeList) throws XmlCustomException {

        Map<Integer, List<ItemResponse>> itemMap = new HashMap<>();

        int errorIndex = 0;

        try {

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node parentNode = nodeList.item(i);

                errorIndex = i;

                if (parentNode == null) {
                    throw new XmlCustomException("DETAIL 에 해당 ParentNode 가 존재하지 않습니다.");
                }

                NodeList childNodes = parentNode.getChildNodes();

                if (childNodes.getLength() < 6) {
                    throw new XmlCustomException("DETAIL 노드의 자식 노드가 예상보다 적습니다. 노드 길이: " + (childNodes != null ? childNodes.getLength() : 0));
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
            throw new XmlCustomException("XML_DATA DETAIL 파싱 중 숫자 형식 파싱 오류가 발생했습니다. 노드 인덱스: " + errorIndex, e);
        } catch (NullPointerException e) {
            throw new XmlCustomException("XML_DATA DETAIL 필수 노드 또는 값이 null입니다. 노드 인덱스: " + errorIndex, e);
        } catch (Exception e) {
            throw new XmlCustomException("XML_DATA DETAIL 파싱 중 알 수 없는 예외가 발생했습니다. 노드 인덱스: " + errorIndex, e);
        }

        return itemMap;
    }

    @Override
    public int insertOrderList(List<OrderInsert> orders, Map<String, String> dbconfig) throws DbCustomException {

        int res = 1;

        Connection conn = null;

        conn = JDBCTemplate.getConnection(dbconfig.get("host"), dbconfig.get("port"), dbconfig.get("sid"), dbconfig.get("user"), dbconfig.get("password"));

        for (OrderInsert orderInsert : orders) {
            for (ItemResponse itemResponse : orderInsert.getItems()) {
                res *= orderDao.insertOrder(conn, orderInsert.getOrder(), itemResponse);
                if (res == 0){
                    throw new DbCustomException("ORDERS 데이터 INSERT에 실패했습니다.");
                }
            }
        }

        JDBCTemplate.close(conn);

        return res;
    }

}
