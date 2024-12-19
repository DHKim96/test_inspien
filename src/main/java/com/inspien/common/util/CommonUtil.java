package com.inspien.common.util;

import com.inspien.common.exception.ParseCustomException;
import com.inspien.common.exception.SoapCustomException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
    /**
     * String 형식인 SOAP 응답 데이터를 HashMap<태그명, 태그 내용> 으로 변환하는 메서드.
     *
     * @param connInfo SOAP 로 응답받은 서버 연결 정보 데이터.
     * @param type 연결 정보 주체(DATABASE, FTP)
     * @return HashMap<태그명, 태그 내용> 으로 변환된 SOAP 응답 데이터.
     * @throws ParseCustomException
     */
    public static Map<String, String> parseConnectionInfoToMap(String connInfo, ConnectionType type) throws ParseCustomException {
        Map<String, String> connInfoMap = new HashMap<>();

        if (!ConnectionType.isValidType(String.valueOf(type))){
            throw new ParseCustomException(ErrCode.CONNECTION_TYPE_UNSUPPORTED, String.valueOf(type));
        }

        // 키셋 가져오기
        String[] keys = type.getKeys();

        // 데이터 유효성 검사
        if (connInfo == null || connInfo.trim().isEmpty()) {
            throw new ParseCustomException(ErrCode.NULL_POINT_ERROR, type + " Connection Info");
        }

        // 공백 기준으로 문자열 분할
        String[] tokens = connInfo.split(" ");

        if (tokens.length != keys.length) {
            throw new ParseCustomException(ErrCode.INVALID_FORMAT,  type + " Connection Info");
        }

        // 키와 값을 매칭하여 Map에 추가
        for (int i = 0; i < keys.length; i++) {
            connInfoMap.put(keys[i], tokens[i]);
        }

        return connInfoMap;
    }

    /**
     * SOAP Response 의 각 태그들 내 요소를 추출하는 메서드
     * @param doc : XML DOM
     * @param tagName : 태그명
     * @return 각 태그별 요소의 TEXT
     */
    public static String getTagValue(Document doc, String tagName) throws ParseCustomException {
        StringBuilder res = new StringBuilder();

        NodeList nodelist = doc.getElementsByTagName(tagName);

        if (nodelist.getLength() == 0) {
            throw new ParseCustomException(ErrCode.XML_ELEMENT_NOT_FOUND, tagName);
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
            throw new ParseCustomException(ErrCode.NULL_POINT_ERROR, tagName + " 태그");
        }

        return res.toString();
    }
}
