package com.inspien.xml.controller;

import com.inspien.common.exception.*;
import com.inspien.common.util.CommonUtil;
import com.inspien.common.util.ConnectionType;
import com.inspien.common.util.ErrCode;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.xml.dto.OrderInsert;
import com.inspien.xml.service.XmlService;
import com.inspien.xml.service.XmlServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * XML 데이터를 처리하고 DB 작업을 수행하는 컨트롤러 클래스.
 */
@Slf4j
public class XmlController {
    private final XmlService xmlService;


    /**
     * 기본 생성자.
     * XmlService 구현체를 초기화합니다.
     */
    public XmlController() {
        this.xmlService = new XmlServiceImpl();
    }

    /**
     * SOAP Response에서 XML 데이터를 처리하고 DB 작업을 수행합니다.
     *
     * @param soapResponse SOAP Response 데이터
     * @return 성공적으로 처리된 데이터 건수
     * @throws XmlDataProcessException XML 데이터 처리 중 오류가 발생한 경우
     */
    public int processXmlData(SoapResponse soapResponse) throws XmlDataProcessException {
        int result = 0;

        try {
            // 2.1. DB 연결 정보 매핑
            Map<String, String> dbConfig = this.parseDBConnectionInfoToMap(soapResponse);
            // 2.2. XML_DATA 핸들링
            List<OrderInsert> orderInserts = this.handleXmlData(soapResponse);
            // 2.3. 핸들링한 XML_DATA DB에 INSERT
            result = this.insertOrderList(orderInserts, dbConfig);
        } catch (ParseCustomException | XmlCustomException | DbCustomException e) {
            throw new XmlDataProcessException(e);
        }

        return result;
    }

    /**
     * SOAP Response에서 DB 연결 정보를 파싱합니다.
     *
     * @param soapResponse SOAP Response 데이터
     * @return DB 연결 정보가 담긴 맵
     * @throws ParseCustomException DB 연결 정보가 없거나 파싱 중 오류가 발생한 경우
     */
    private Map<String, String> parseDBConnectionInfoToMap(SoapResponse soapResponse) throws ParseCustomException {
        Map<String, String> dbConfig = CommonUtil.parseConnectionInfoToMap(soapResponse.getDbConnInfo(), ConnectionType.DATABASE);

        if (dbConfig.isEmpty()) {
            throw new ParseCustomException(ErrCode.NULL_POINT_ERROR, "dbConfig");
        }

        return dbConfig;
    }

    /**
     * SOAP Response에서 XML 데이터를 파싱하여 OrderInsert 리스트로 변환합니다.
     *
     * @param soapResponse SOAP Response 데이터
     * @return OrderInsert 객체 리스트
     * @throws XmlCustomException XML 데이터 처리 중 오류가 발생한 경우
     */
    private List<OrderInsert> handleXmlData(SoapResponse soapResponse) throws XmlCustomException {
        List<OrderInsert> orderInserts = xmlService.handleXmlData(soapResponse.getXmlData());

        if (orderInserts == null) {
            throw new XmlCustomException(ErrCode.NULL_POINT_ERROR, "OrderInserts");
        }

        return orderInserts;
    }

    /**
     * 파싱된 XML 데이터를 DB에 저장합니다.
     *
     * @param orderInserts 저장할 OrderInsert 리스트
     * @param dbConfig     DB 연결 설정 정보
     * @return 성공적으로 삽입된 레코드 수
     * @throws DbCustomException DB 삽입 중 오류가 발생한 경우
     */
    private int insertOrderList(List<OrderInsert> orderInserts, Map<String, String> dbConfig) throws DbCustomException {
        return xmlService.insertOrderList(orderInserts, dbConfig);
    }
}
