package com.inspien.json.controller;

import com.inspien.common.exception.FtpCustomException;
import com.inspien.common.exception.JsonCustomException;
import com.inspien.common.util.CommonUtil;
import com.inspien.common.util.ConnectionType;
import com.inspien.json.dto.RecordResponse;
import com.inspien.json.service.JsonService;
import com.inspien.json.service.JsonServiceImpl;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;

import java.util.List;
import java.util.Map;

public class JsonController {

    private final JsonService jsonService;

    public JsonController() {
        this.jsonService = new JsonServiceImpl();
    }

    public void processJsonData(SoapResponse soapResponse, User user) {
        // 3.1. FTP 연결 정보 매핑
        Map<String, String> ftpConfig = this.parseFTPConnectionInfoToMap(soapResponse);
        // 3.2. JSON_DATA 핸들링
        List<RecordResponse> recordResponses = this.jsonDataToRecordList(soapResponse);
        // 3.3. FLATFILE 파일명 생성
        String fileName = this.createSaveFileName(user);
        // 3.4. Properties 에 있는 local 저장 경로 로드
        String localUploadPath = this.loadLocalUploadPath();
        // 3.5. JSON_DATE FLATFILE 로 변환
        this.convertToFlatFile(recordResponses, localUploadPath, fileName);
        // 3.6. FLATFILE FTP 서버에 INSERT
        boolean isUploaded = this.uploadFileToFtp(ftpConfig, localUploadPath, fileName);
    }

    private Map<String, String> parseFTPConnectionInfoToMap(SoapResponse soapResponse){
        Map<String, String> ftpConfig = null;

        try {
            ftpConfig = CommonUtil.parseConnectionInfoToMap(soapResponse.getFtpConnInfo(), ConnectionType.FTP);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        return ftpConfig;
    }

    private List<RecordResponse> jsonDataToRecordList(SoapResponse soapResponse){
        List<RecordResponse> recordResponses = null;

        try {
            recordResponses = jsonService.jsonDataToList(soapResponse.getJsonData(), "record", RecordResponse.class);
        } catch (JsonCustomException e){
            e.printStackTrace();
        }

        return recordResponses;
    }

    private String createSaveFileName(User user) {
        String fileName = null;

        try {
            fileName = jsonService.createSaveFileName(user);
        } catch (JsonCustomException e){
            e.printStackTrace();
        }

        return fileName;
    }

    private String loadLocalUploadPath(){

        String localUploadPath = null;

        try {
            localUploadPath = jsonService.loadLocalUploadPath();

        } catch (JsonCustomException e){
            e.printStackTrace();
        }

        return localUploadPath;
    }

    private void convertToFlatFile(List<RecordResponse> recordResponses, String localUploadPath, String fileName) {

        try {
            jsonService.convertToFlatFile(recordResponses, localUploadPath, fileName);
        } catch (FtpCustomException e) {
            e.printStackTrace();
        }
    }

    private boolean uploadFileToFtp(Map<String, String> ftpConfig, String localFilePath, String fileName){

        boolean isUploaded = false;

        try {
            isUploaded = jsonService.uploadFileToFtp(ftpConfig, localFilePath, fileName);
        } catch (FtpCustomException e){
            e.printStackTrace();
        }

        return isUploaded;
    }
}
