package com.inspien.service;

import com.inspien.exception.SoapServiceException;
import com.inspien.model.dto.*;
import com.inspien.model.dto.Record;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SoapService {

    String loadEndPoint();

    SOAPConnection createSoapConnection() throws SOAPException, SoapServiceException;

    String getSoapResponseAsString(SOAPMessage response) throws SoapServiceException ;

    SoapResponse parseSoapXML(String response) throws SoapServiceException;

    void createSoapEnvelope(SOAPMessage soapMessage, User user) throws SoapServiceException;

    String requestSoapWebService(User user) throws SoapServiceException;

    SOAPMessage createSoapRequest(String soapAction, User user) throws SoapServiceException;

    int insertOrderList(List<OrderInsert> orders, Map<String, String> dbconfig);

    String getTagValue(Document doc, String tagName) throws SoapServiceException;

    List<OrderInsert> handleXmlDatas(String xmlData) throws SoapServiceException;

    Map<Integer, OrderResponse> parseXmlDatasHeader(NodeList nodeList);

    Map<Integer, List<ItemResponse>> parseXmlDatasDetail(NodeList nodeList);

    <T> List<T> jsonDataToList(String jsonData, String tagName);

    Map<String, String> connInfoToMap(String connInfo, int type);

    void convertToFlatFile(List<Record> records, String localUploadPath, String fileName);

    String createSaveFileName(String name);

    boolean uploadFileToFtp(Map<String, String> ftpconfig, String localUploadPath, String fileName);
}
