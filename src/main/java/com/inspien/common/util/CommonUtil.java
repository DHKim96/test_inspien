package com.inspien.common.util;

import com.inspien.common.exception.ErrCode;
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
     * @throws IllegalArgumentException ConnectionType 내에 존재하지 않는 type 일 경우
     */
    public static Map<String, String> parseConnectionInfoToMap(String connInfo, ConnectionType type) throws IllegalArgumentException {
        Map<String, String> connInfoMap = new HashMap<>();

        // 키셋 정의
        String[] keys;
        switch (type) {
            case DATABASE -> keys = new String[]{"host", "port", "sid", "user", "password", "tablename"};
            case FTP -> keys = new String[]{"host", "port", "user", "password", "filepath"};
            default -> throw new IllegalArgumentException(ErrCode.CONNECTION_TYPE_UNSUPPORTED.getCode() + ErrCode.CONNECTION_TYPE_UNSUPPORTED.getMsg(type.toString()));
        }

        // 데이터 유효성 검사
        if (connInfo == null || connInfo.trim().isEmpty()) {
            throw new IllegalArgumentException(type + " Connection Info 가 비어있습니다.");
        }

        // 공백 기준으로 문자열 분할
        String[] tokens = connInfo.split(" ");

        if (tokens.length != keys.length) {
            throw new IllegalArgumentException(type + " Connection Info의 형식이 잘못되었습니다. 필요한 데이터 개수: " + keys.length);
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
    public static String getTagValue(Document doc, String tagName) throws SoapCustomException {
        StringBuilder res = new StringBuilder();

        NodeList nodelist = doc.getElementsByTagName(tagName);

        if (nodelist.getLength() == 0) {
            throw new SoapCustomException("WSDL 에서 [%s] 태그가 존재하지 않습니다.".formatted(tagName));
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
            throw new SoapCustomException("[%s] 태그 내에 값이 존재하지 않습니다.".formatted(tagName));
        }

        return res.toString();
    }
}
