package com.inspien.service;

import com.inspien.model.dto.User;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;

public interface SoapService {

    String requestSoap(User user);

    void parseSoapXML(String response) throws SOAPException;

    void createSoapEnvelope(SOAPMessage soapMessage, User user) throws SOAPException;

    String requestSoapWebService(User user) throws SOAPException;

    SOAPMessage createSoapRequest(String soapAction, User user) throws SOAPException;
}
