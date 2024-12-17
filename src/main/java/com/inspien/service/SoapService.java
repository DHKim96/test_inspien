package com.inspien.service;

import com.inspien.model.dto.*;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Map;

public interface SoapService {

    SoapResponse parseSoapXML(String response) throws SOAPException;

    void createSoapEnvelope(SOAPMessage soapMessage, User user) throws SOAPException;

    String requestSoapWebService(User user) throws SOAPException;

    SOAPMessage createSoapRequest(String soapAction, User user) throws SOAPException;

    int insertOrderList(List<OrderInsert> orders);

    String getTagValue(Document doc, String tagName) throws Exception;

    List<OrderInsert> handleXmlDatas(String xmlData);

    Map<Integer, OrderResponse> parseXmlDatasHeader(NodeList nodeList);

    Map<Integer, List<ItemResponse>> parseXmlDatasDetail(NodeList nodeList);
}
