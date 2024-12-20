package com.inspien.common.util;

import com.inspien.common.exception.ParseCustomException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * 공통적으로 사용되는 유틸리티 메서드 모음 클래스.
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>SOAP 연결 정보 데이터를 Map으로 변환</li>
 *     <li>XML 태그의 값을 추출</li>
 * </ul>
 */
public class CommonUtil {


    /**
     * SOAP 응답 데이터를 Map 형태로 변환합니다.
     * 주어진 연결 정보 문자열을 공백으로 구분하여 키와 값을 매핑합니다.
     *
     * @param connInfo SOAP로 응답받은 서버 연결 정보 데이터
     * @param type     연결 정보 주체 ({@link ConnectionType})
     * @return 변환된 연결 정보
     * @throws ParseCustomException 연결 정보가 유효하지 않거나 포맷이 잘못된 경우 발생
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
     * XML DOM에서 특정 태그의 값을 추출합니다.
     * 주어진 태그명으로 XML 문서를 검색한 후, 해당 태그의 텍스트 내용을 반환합니다.
     *
     * @param doc     XML DOM 객체
     * @param tagName 검색할 태그명
     * @return 태그의 텍스트 내용
     * @throws ParseCustomException 태그가 존재하지 않거나 내용이 비어 있는 경우 발생
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
