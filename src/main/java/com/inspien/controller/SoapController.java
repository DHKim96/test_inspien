package com.inspien.controller;

import com.inspien.exception.SoapServiceException;
import com.inspien.model.dto.*;
import com.inspien.model.dto.Record;
import com.inspien.service.SoapService;
import com.inspien.service.SoapServiceImpl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SoapController {
    private final SoapService soapService;
    private final String localUploadPath;

    public SoapController() {
        this.soapService = new SoapServiceImpl();
        this.localUploadPath = this.loadLocalUploadPath();
    }

    public void executeService(String name, String phone, String email) {
        try {
            // 1. 서비스 호출
            SoapResponse soapResponse = this.callAndParseSoapService(name, phone, email);

            // 2. XML_DATA INSERT
            processXmlData(soapResponse);

            // 3. JSON_DATA UPLOAD
            processJsonData(soapResponse, name);
        } catch (SoapServiceException e){

        }
    }

    private SoapResponse callAndParseSoapService(String name, String phone, String email) throws SoapServiceException {
        User user = User.builder().name(name).phone(phone).email(email).build();
        // 1.1. SOAP Request 전송
        String res = soapService.requestSoapWebService(user);
        // 1.2. SOAP Response 파싱
        return soapService.parseSoapXML(res);
    }

    private void processXmlData(SoapResponse soapResponse) throws SoapServiceException {
        // 2.1. DB 연결 정보 매핑
        Map<String, String> dbconfig = soapService.connInfoToMap(soapResponse.getDbConnInfo(), 1);
        // 2.2. XML_DATA 핸들링
        List<OrderInsert> orderInserts = soapService.handleXmlDatas(soapResponse.getXmlData());
        // 2.3. 핸들링한 XML_DATA DB에 INSERT
        int result = soapService.insertOrderList(orderInserts, dbconfig);
        if (result == 1) {
            System.out.println("INSERT 성공");
        } else {
            System.out.println("INSERT 실패");
        }
    }

    private void processJsonData(SoapResponse soapResponse, String name) {
        // 3.1. FTP 연결 정보 매핑
        Map<String, String> ftpconfig = soapService.connInfoToMap(soapResponse.getFtpConnInfo(), 2);
        // 3.2. JSON_DATA 핸들링
        List<Record> records = soapService.jsonDataToList(soapResponse.getJsonData(), "record");
        // 3.3. FLATFILE 파일명 생성
        String fileName = soapService.createSaveFileName(name);
        // 3.4. JSON_DATE FLATFILE 로 변환
        soapService.convertToFlatFile(records, localUploadPath, fileName);
        // 3.5. FLATFILE FTP 서버에 INSERT
        boolean isUploaded = soapService.uploadFileToFtp(ftpconfig, localUploadPath, fileName);
        if (isUploaded) {
            System.out.println("File uploaded successfully: " + fileName);
        } else {
            System.err.println("File upload failed: " + fileName);
        }
    }

    private String loadLocalUploadPath(){
        String localUploadPath = null;

        Properties prop = new Properties();

        try(InputStream input = getClass().getClassLoader().getResourceAsStream("client.properties")){
            if (input == null) {
                throw new FileNotFoundException("property file 'client.properties' not found in the classpath");
            }

            prop.load(input);

            localUploadPath = prop.getProperty("local.upload.path");

            if (localUploadPath == null) {
                throw new Exception("client.properties 에 localUploadPath 가 존재하지 않습니다.");
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return localUploadPath;
    }
}
