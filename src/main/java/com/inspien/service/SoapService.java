package com.inspien.service;

import com.inspien.model.dto.*;
import com.inspien.model.dto.Record;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Map;

public interface SoapService {

    String loadEndPoint();

    SoapResponse parseSoapXML(String response) throws SOAPException;

    void createSoapEnvelope(SOAPMessage soapMessage, User user) throws SOAPException;

    String requestSoapWebService(User user) throws SOAPException;

    SOAPMessage createSoapRequest(String soapAction, User user) throws SOAPException;

    int insertOrderList(List<OrderInsert> orders, Map<String, String> dbconfig);

    String getTagValue(Document doc, String tagName) throws Exception;

    List<OrderInsert> handleXmlDatas(String xmlData);

    Map<Integer, OrderResponse> parseXmlDatasHeader(NodeList nodeList);

    Map<Integer, List<ItemResponse>> parseXmlDatasDetail(NodeList nodeList);

    List<Record> handleJsonDatas(String jsonData);

    Map<String, String> connInfoToMap(String connInfo, int type);

    void convertToFlatFile(List<Record> records, String filepath);
}
