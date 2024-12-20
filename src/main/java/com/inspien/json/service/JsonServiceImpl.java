package com.inspien.json.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspien.common.exception.FtpCustomException;
import com.inspien.common.exception.JsonCustomException;
import com.inspien.common.exception.SoapCustomException;
import com.inspien.common.util.ErrCode;
import com.inspien.common.util.FtpClientUtil;
import com.inspien.common.validation.UserValidator;
import com.inspien.json.dto.RecordResponse;
import com.inspien.soap.dto.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JsonService 인터페이스의 구현 클래스.
 * JSON 데이터 파싱, 플랫 파일 변환 및 FTP 업로드 기능을 제공합니다.
 */
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
                throw new JsonCustomException(ErrCode.JSON_FIELD_NOT_FOUND, tagName);
            } else if (!chileNode.isArray()){
                throw new JsonCustomException(ErrCode.JSON_NOT_ARRAY, tagName);
            }

            res = objectMapper.readValue(
                    chileNode.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, tClass)
            );
        } catch (JsonProcessingException e) {
            throw new JsonCustomException(ErrCode.JSON_NOT_MAPPED, tagName, e);
        }

        return res;
    }

    @Override
    public String loadLocalUploadPath() throws JsonCustomException {
        String localUploadPath = null;

        Properties prop = new Properties();

        String fileName = "client.properties";

        try(InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)){
            if (input == null) {
                throw new JsonCustomException(ErrCode.FILE_NOT_FOUND, fileName);
            }

            prop.load(input);

            localUploadPath = prop.getProperty("local.upload.path");

            if (localUploadPath == null) {
                throw new JsonCustomException(ErrCode.PROPERTY_NOT_FOUND, fileName, localUploadPath);
            }

        } catch (IOException e) {
            throw new JsonCustomException(ErrCode.IO_STREAM_ERROR, e);
        }

        return localUploadPath;
    }

    @Override
    public String createSaveFileName(User user) throws SoapCustomException {
        UserValidator validator = new UserValidator();
        // 유효성 검증
        validator.validateName(user.getName());

        String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        return String.format("INSPIEN_JSON_[%s]_[%s].txt", user.getName(), currentTime);
    }

    @Override
    public void convertToFlatFile(List<RecordResponse> recordResponses, String localUploadPath, String fileName) throws FtpCustomException {
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
            throw new FtpCustomException(ErrCode.FILE_CREATE_FAILED, e);
        }
    }

    @Override
    public boolean uploadFileToFtp(Map<String, String> ftpConfig, String localFilePath, String fileName) throws FtpCustomException {
        boolean isUploaded = false;
        FtpClientUtil ftpClientUtil = null;

        try {
            // 입력값 검증
            if (ftpConfig == null || ftpConfig.isEmpty()) {
                throw new FtpCustomException(ErrCode.NULL_POINT_ERROR, "FTP 연결 정보");
            }

            if (localFilePath == null || localFilePath.isEmpty()) {
                throw new FtpCustomException(ErrCode.NULL_POINT_ERROR, "로컬 저장 경로");
            }

            if (fileName == null || fileName.isEmpty()) {
                throw new FtpCustomException(ErrCode.NULL_POINT_ERROR, "파일명");
            }

            // FTP 연결 정보 추출
            String host = ftpConfig.get("host");
            String portString = ftpConfig.get("port");
            String user = ftpConfig.get("user");
            String password = ftpConfig.get("password");
            String filepath = ftpConfig.get("filepath");

            if (host == null || portString == null || user == null || password == null || filepath == null) {
                throw new FtpCustomException(ErrCode.NULL_POINT_ERROR, "FTP 설정 정보 중 필수 값이 누락되었습니다.");
            }

            int port;

            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                throw new FtpCustomException(ErrCode.INVALID_FORMAT, "FTP 서버 PORT", e);
            }

            String localUploadPath = localFilePath + fileName;
            String remoteUploadPath = filepath + fileName;

            ftpClientUtil = new FtpClientUtil(host, port, user, password);

            isUploaded = ftpClientUtil.uploadFile(localUploadPath, remoteUploadPath);

            if (!isUploaded) {
                throw new FtpCustomException(ErrCode.FTP_UPLOAD_FAILED);
            }

        } catch (Exception e){
            throw new FtpCustomException(ErrCode.UNKNOWN_ERROR, e);
        } finally {
            ftpClientUtil.disconnect();
        }


        return isUploaded;
    }
}
