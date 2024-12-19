package com.inspien.json.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspien.common.exception.FtpCustomException;
import com.inspien.common.exception.JsonCustomException;
import com.inspien.common.util.FtpClientUtil;
import com.inspien.common.validation.UserValidator;
import com.inspien.json.dto.RecordResponse;
import com.inspien.soap.dto.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class JsonServiceImpl implements JsonService {

    @Override
    public <T> List<T> jsonDataToList(String jsonData, String tagName, Class<T> tClass) throws JsonCustomException {
        List<T> res;

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 해당 필드를 가져와 List<해당 필드> 로 변환
            JsonNode rootNode = objectMapper.readTree(jsonData);

            JsonNode chileNode = rootNode.get(tagName);

            if (chileNode == null) {
                throw new JsonCustomException(String.format("JSON 데이터에서 [%s] 값이 존재하지 않습니다.", tagName));
            } else if (!chileNode.isArray()){
                throw new JsonCustomException(String.format("JSON 데이터에서 [%s] 값이 배열 형태가 아닙니다.", tagName));
            }

            res = objectMapper.readValue(
                    chileNode.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, tClass)
            );
        } catch (JsonProcessingException e) {
            throw new JsonCustomException(String.format("JSON 데이터를 %s 인스턴스로 역직렬화 중 JSON 매핑에 실패했습니다.", tagName), e);
        }

        return res;
    }

    @Override
    public String loadLocalUploadPath() {
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

    @Override
    public String createSaveFileName(User user) {
        UserValidator validator = new UserValidator();
        // 유효성 검증
        validator.validateName(user.getName());

        String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        return String.format("INSPIEN_JSON_[%s]_[%s].txt", user.getName(), currentTime);
    }

    @Override
    public void convertToFlatFile(List<RecordResponse> recordResponses, String localUploadPath, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(localUploadPath + fileName))) {
            for (RecordResponse recordResponse : recordResponses) {
                // 각 필드를 |로 구분해 한 줄로 작성
                String flatLine = String.join("^",
                        recordResponse.getNames(),
                        recordResponse.getPhone(),
                        recordResponse.getEmail(),
                        recordResponse.getBirthday(),
                        recordResponse.getCompany(),
                        recordResponse.getPersonalNumber(),
                        recordResponse.getOrganizationNumber(),
                        recordResponse.getCountry(),
                        recordResponse.getRegion(),
                        recordResponse.getCity(),
                        recordResponse.getStreet(),
                        recordResponse.getZipCode(),
                        recordResponse.getCreditCard(),
                        recordResponse.getGuid()
                ) + "\\n";

                writer.write(flatLine);
                writer.newLine(); // 다음 줄로 이동
            }
        } catch (IOException e) {
            throw new FtpCustomException("jsonData to Flat file 생성 실패 에러 메시지 : " + e.getMessage(), e);
        }
    }

    @Override
    public boolean uploadFileToFtp(Map<String, String> ftpConfig, String localFilePath, String fileName) throws FtpCustomException {
        boolean isUploaded = false;
        FtpClientUtil ftpClientUtil = null;

        try {
            // 입력값 검증
            if (ftpConfig == null || ftpConfig.isEmpty()) {
                throw new FtpCustomException("FTP 연결 정보가 비어있습니다.");
            }

            if (localFilePath == null || localFilePath.isEmpty()) {
                throw new FtpCustomException("로컬 파일 저장 경로가 비어있습니다.");
            }

            if (fileName == null || fileName.isEmpty()) {
                throw new FtpCustomException("파일명이 존재하지 않습니다.");
            }

            // FTP 연결 정보 추출
            String host = ftpConfig.get("host");
            String portString = ftpConfig.get("port");
            String user = ftpConfig.get("user");
            String password = ftpConfig.get("password");
            String filepath = ftpConfig.get("filepath");

            if (host == null || portString == null || user == null || password == null || filepath == null) {
                throw new FtpCustomException("FTP 설정 정보 중 필수 값이 누락되었습니다.");
            }

            int port;

            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                throw new FtpCustomException("포트 정보가 숫자 형식이 아닙니다: " + portString, e);
            }

            String localUploadPath = localFilePath + fileName;
            String remoteUploadPath = filepath + fileName;

            ftpClientUtil = new FtpClientUtil(host, port, user, password);

            isUploaded = ftpClientUtil.uploadFile(localUploadPath, remoteUploadPath);

            if (!isUploaded) {
                throw new FtpCustomException("파일 업로드에 실패했습니다. 경로: " + remoteUploadPath);
            }

        } catch (Exception e){
            throw new FtpCustomException("알 수 없는 오류가 발생했습니다. 에러 메시지 : " + e.getMessage(), e);
        } finally {
            ftpClientUtil.disconnect();
        }


        return isUploaded;
    }

}
