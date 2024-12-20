package com.inspien.xml.service;

import com.inspien.common.exception.DbCustomException;
import com.inspien.common.exception.SoapCustomException;
import com.inspien.common.exception.XmlCustomException;
import com.inspien.common.util.ErrCode;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XmlService 인터페이스의 구현 클래스.
 * XML 데이터를 처리하고 DB에 저장하는 기능을 제공합니다.
 */
public class XmlServiceImpl implements XmlService {

    private final String ENCODING;

    private final OrderDao orderDao;

    /**
     * 기본 생성자.
     * UTF-8 인코딩을 사용하고 OrderDao를 초기화합니다.
     */
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

        String[] keys = {"HEADER", "DETAIL"};

        try {
            // XML 파싱
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            NodeList headerNodeList = doc.getElementsByTagName(keys[0]);
            if (headerNodeList.getLength() == 0) {
                throw new XmlCustomException(ErrCode.XML_ELEMENT_NOT_FOUND, keys[0]);
            }
            Map<Integer, OrderResponse> ordMap = parseXmlDataHeader(headerNodeList);

            NodeList detailNodeList = doc.getElementsByTagName(keys[1]);
            if (detailNodeList.getLength() == 0) {
                throw new XmlCustomException(ErrCode.XML_ELEMENT_NOT_FOUND, keys[1]);
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
            throw new XmlCustomException(ErrCode.SOAP_DOCUMENT_BUILDER_NOT_CREATED, e);
        } catch (IOException e) {
            throw new XmlCustomException(ErrCode.IO_STREAM_ERROR, e);
        } catch (SAXException e) {
            throw new XmlCustomException(ErrCode.INVALID_FORMAT, "XML_DATA", e);
        }

        return orderInserts;
    }


    /**
     * HEADER 태그의 데이터를 파싱하여 OrderResponse 맵으로 변환합니다.
     *
     * @param nodeList HEADER 태그의 NodeList
     * @return OrderResponse 객체를 담은 맵 (키: orderNum)
     * @throws XmlCustomException HEADER 데이터 파싱 중 오류가 발생한 경우
     */
    private Map<Integer, OrderResponse> parseXmlDataHeader(NodeList nodeList) throws XmlCustomException {

        String key = "HEADER";
        Map<Integer, OrderResponse> ordMap = new HashMap<>();
        int childNodesNum = 10;

        for (int parentNodeIdx = 0; parentNodeIdx < nodeList.getLength(); parentNodeIdx++) {
            Node parentNode = nodeList.item(parentNodeIdx);


            if (parentNode == null) {
                throw new XmlCustomException(ErrCode.XML_ELEMENT_NOT_FOUND, String.format("%s %s번째 노드", key, parentNodeIdx));
            }

            NodeList childNodes = parentNode.getChildNodes();

            if (childNodes.getLength() < childNodesNum) {
                throw new XmlCustomException(ErrCode.XML_CHILD_NODE_LESS, key);
            }

            int childNodeIdx = 0;

            try {
                int orderNum = Integer.parseInt(childNodes.item(childNodeIdx++).getTextContent());
                String orderId = childNodes.item(childNodeIdx++).getTextContent();
                String orderDate = childNodes.item(childNodeIdx++).getTextContent();
                int orderPrice = Integer.parseInt(childNodes.item(childNodeIdx++).getTextContent());
                int orderQty = Integer.parseInt(childNodes.item(childNodeIdx++).getTextContent());
                String receiverName = childNodes.item(childNodeIdx++).getTextContent();
                String receiverNo = childNodes.item(childNodeIdx++).getTextContent();
                String etaDate = childNodes.item(childNodeIdx++).getTextContent();
                String destination = childNodes.item(childNodeIdx++).getTextContent();
                String description = childNodes.item(childNodeIdx++).getTextContent();

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

            } catch (NullPointerException e) {
                throw new XmlCustomException(ErrCode.XML_ELEMENT_NOT_FOUND, String.format("%s %s번째 노드의 %s번째 요소", key, parentNodeIdx, childNodeIdx));
            } catch (NumberFormatException e) {
                throw new XmlCustomException(ErrCode.INVALID_FORMAT, String.format("%s %s번째 노드의 %s번째 요소", key, parentNodeIdx, childNodeIdx));
            }
        }

        return ordMap;
    }


    /**
     * DETAIL 태그의 데이터를 파싱하여 ItemResponse 맵으로 변환합니다.
     *
     * @param nodeList DETAIL 태그의 NodeList
     * @return ItemResponse 객체를 담은 맵 (키: orderNum)
     * @throws XmlCustomException DETAIL 데이터 파싱 중 오류가 발생한 경우
     */
    private Map<Integer, List<ItemResponse>> parseXmlDataDetail(NodeList nodeList) throws XmlCustomException {

        String key = "DETAIL";

        Map<Integer, List<ItemResponse>> itemMap = new HashMap<>();

        int parentNodeIdx;
        int childNodeIdx;

        for (parentNodeIdx = 0; parentNodeIdx < nodeList.getLength(); parentNodeIdx++) {
            Node parentNode = nodeList.item(parentNodeIdx);

            if (parentNode == null) {
                throw new XmlCustomException(ErrCode.XML_ELEMENT_NOT_FOUND, String.format("%s %s번째 노드", key, parentNodeIdx));
            }

            childNodeIdx = 0;
            NodeList childNodes = parentNode.getChildNodes();

            if (childNodes.getLength() < 6) {
                throw new XmlCustomException(ErrCode.XML_CHILD_NODE_LESS, key);
            }

            try {
                int orderNum = Integer.parseInt(childNodes.item(childNodeIdx++).getTextContent());
                int itemSeq = Integer.parseInt(childNodes.item(childNodeIdx++).getTextContent());
                String itemName = childNodes.item(childNodeIdx++).getTextContent();
                int itemQty = Integer.parseInt(childNodes.item(childNodeIdx++).getTextContent());
                String itemColor = childNodes.item(childNodeIdx++).getTextContent();
                int itemPrice = Integer.parseInt(childNodes.item(childNodeIdx++).getTextContent());

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

            } catch (NullPointerException e){
                throw new XmlCustomException(ErrCode.XML_ELEMENT_NOT_FOUND, String.format("%s %s번째 노드의 %s번째 요소", key, parentNodeIdx, childNodeIdx));
            }
        }

        return itemMap;
    }


    @Override
    public int insertOrderList(List<OrderInsert> orders, Map<String, String> dbConfig) throws DbCustomException {

        int successCount = 0;

        Connection conn = JDBCTemplate.getConnection(
                dbConfig.get("host"), dbConfig.get("port"), dbConfig.get("sid"),
                dbConfig.get("user"), dbConfig.get("password")
                );


        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DbCustomException(ErrCode.DATABASE_AUTO_COMMIT_ERROR, e);
        }

        try {
            for (OrderInsert orderInsert : orders) {
                for (ItemResponse itemResponse : orderInsert.getItems()) {
                    int result = orderDao.insertOrder(conn, orderInsert.getOrder(), itemResponse);
                    if (result == 0){
                        // 실패 시 롤백
                        conn.rollback();
                        throw new DbCustomException(ErrCode.DATABASE_INSERT_FAILED, dbConfig.get("tablename"), "ORDER");
                    }
                    successCount++;
                }
            }

            conn.commit();
        } catch (SQLException e) {
            // 예외 발생 시 롤백
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                throw new DbCustomException(ErrCode.DATABASE_ROLLBACK_FAILED, rollbackEx);
            }

            throw new DbCustomException(ErrCode.DATABASE_INSERT_FAILED, dbConfig.get("tablename"), "ORDER");
        }

        JDBCTemplate.close(conn);

        return successCount;
    }

}
