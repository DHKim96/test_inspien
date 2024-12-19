package com.inspien.soap.service;

import com.inspien.common.exception.SoapCustomException;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public interface SoapService {
    String requestSoapWebService(User user, String endPoint) throws SoapCustomException;

    SoapResponse parseSoapXML(String res) throws SoapCustomException;

    String loadEndPoint() throws SoapCustomException;
}
