package com.inspien.xml.service;

import com.inspien.xml.dto.OrderInsert;

import java.util.List;
import java.util.Map;

public interface XmlService {
    List<OrderInsert> handleXmlData(String xmlData);

    int insertOrderList(List<OrderInsert> orderInserts, Map<String, String> dbconfig);
}
