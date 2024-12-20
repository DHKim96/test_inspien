package com.inspien.xml.service;

import com.inspien.common.exception.DbCustomException;
import com.inspien.common.exception.XmlCustomException;
import com.inspien.xml.dto.OrderInsert;

import java.util.List;
import java.util.Map;

/**
 * XML 데이터를 처리하고 DB에 저장하는 서비스 인터페이스.
 */
public interface XmlService {


    /**
     * XML 데이터를 파싱하여 OrderInsert 객체 리스트로 변환합니다.
     *
     * @param xmlData XML 데이터 (문자열 형태)
     * @return OrderInsert 객체 리스트
     * @throws XmlCustomException XML 데이터 처리 중 오류가 발생한 경우
     */
    List<OrderInsert> handleXmlData(String xmlData) throws XmlCustomException;

    /**
     * 파싱된 OrderInsert 데이터를 DB에 저장합니다.
     *
     * @param orderInserts 저장할 OrderInsert 리스트
     * @param dbConfig     DB 연결 설정 정보 (host, port, sid, user, password 등)
     * @return 성공적으로 삽입된 레코드 수
     * @throws DbCustomException DB 삽입 중 오류가 발생한 경우
     */
    int insertOrderList(List<OrderInsert> orderInserts, Map<String, String> dbConfig) throws DbCustomException;
}
