package com.inspien.json.controller;

import com.inspien.common.exception.FtpCustomException;
import com.inspien.common.exception.JsonCustomException;
import com.inspien.common.exception.ParseCustomException;
import com.inspien.common.exception.SoapCustomException;
import com.inspien.common.util.CommonUtil;
import com.inspien.common.util.ConnectionType;
import com.inspien.common.util.ErrCode;
import com.inspien.json.dto.RecordResponse;
import com.inspien.json.service.JsonService;
import com.inspien.json.service.JsonServiceImpl;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


/**
 * JSON 데이터를 처리하고 FTP 서버에 업로드하는 컨트롤러 클래스.
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>FTP 연결 정보 매핑</li>
 *     <li>JSON 데이터를 객체 리스트로 변환</li>
 *     <li>로컬 저장 파일 경로 생성 및 파일 변환</li>
 *     <li>FTP 서버에 파일 업로드</li>
 * </ul>
 */
@Slf4j
public class JsonController {

    private final JsonService jsonService;

    /**
     * 기본 생성자.
     * JsonService 구현체를 초기화합니다.
     */
    public JsonController() {
        this.jsonService = new JsonServiceImpl();
    }

    /**
     * SOAP 응답 데이터를 기반으로 JSON 데이터를 처리합니다.
     *
     * @param soapResponse SOAP 응답 데이터
     * @param user         사용자 정보
     * @return 처리 성공 여부
     * @throws ParseCustomException 데이터 파싱 중 예외가 발생한 경우
     * @throws FtpCustomException 파일 생성 혹은 FTP 서버 업로드 중 예외가 발생한 경우
     * @throws JsonCustomException JSON 데이터 처리 중 예외가 발생한 경우
     * @throws SoapCustomException 유저 정보가 유효하지 않을 시
     */
    public boolean processJsonData(SoapResponse soapResponse, User user) throws ParseCustomException, FtpCustomException, JsonCustomException, SoapCustomException {
        boolean isSuccess = false;

        // 3.1. FTP 연결 정보 매핑
        Map<String, String> ftpConfig = this.parseFTPConnectionInfoToMap(soapResponse);
        // 3.2. JSON_DATA 핸들링
        String tagName = "record";
        List<RecordResponse> recordResponses = this.jsonDataToRecordList(soapResponse, tagName);
        // 3.3. FLATFILE 파일명 생성
        String fileName = this.createSaveFileName(user);
        // 3.4. Properties 에 있는 local 저장 경로 로드
        String localUploadPath = this.loadLocalUploadPath();
        // 3.5. JSON_DATE FLATFILE 로 변환
        this.convertToFlatFile(recordResponses, localUploadPath, fileName);
        // 3.6. FLATFILE FTP 서버에 INSERT
        isSuccess = this.uploadFileToFtp(ftpConfig, localUploadPath, fileName);


        return isSuccess;
    }


    /**
     * FTP 연결 정보를 파싱합니다.
     *
     * @param soapResponse SOAP 응답 데이터
     * @return FTP 연결 정보 맵
     * @throws ParseCustomException 파싱 중 예외가 발생 시
     * @throws FtpCustomException   FTP 정보가 누락된 경우
     */
    private Map<String, String> parseFTPConnectionInfoToMap(SoapResponse soapResponse) throws ParseCustomException, FtpCustomException {
        Map<String, String> ftpConfig = CommonUtil.parseConnectionInfoToMap(soapResponse.getFtpConnInfo(), ConnectionType.FTP);

        if (ftpConfig.isEmpty()){
            throw new FtpCustomException(ErrCode.NULL_POINT_ERROR, "ftpConfig");
        }

        return ftpConfig;
    }


    /**
     * JSON 데이터를 {@link RecordResponse} 객체 리스트로 변환합니다.
     *
     * @param soapResponse SOAP 응답 데이터
     * @param tagName JSON XML 태그명
     * @return JSON 데이터로 변환된 객체 리스트
     * @throws JsonCustomException JSON 데이터 변환 중 오류 발생 시
     *
     */
    private List<RecordResponse> jsonDataToRecordList(SoapResponse soapResponse, String tagName) throws JsonCustomException {

        List<RecordResponse> recordResponses = jsonService.jsonDataToList(soapResponse.getJsonData(), tagName, RecordResponse.class);

        if (recordResponses.isEmpty()){
            throw new JsonCustomException(ErrCode.NULL_POINT_ERROR, tagName);
        }

        return recordResponses;
    }

    /**
     * 파일명을 생성합니다.
     *
     * @param user 사용자 정보
     * @return 생성된 파일명
     * @throws FtpCustomException 파일명 생성 중 예외가 발생 시
     * @throws SoapCustomException 유저 정보가 유효하지 않을 시
     */
    private String createSaveFileName(User user) throws FtpCustomException, SoapCustomException {
        String fileName = jsonService.createSaveFileName(user);

        if (fileName.isEmpty()){
            throw new FtpCustomException(ErrCode.NULL_POINT_ERROR, "fileName");
        }

        return fileName;
    }

    /**
     * 로컬 저장 경로를 로드합니다.
     *
     * @return 로컬 저장 경로
     * @throws JsonCustomException 경로 로드 중 오류 발생 시
     */
    private String loadLocalUploadPath() throws JsonCustomException {

        String localUploadPath = jsonService.loadLocalUploadPath();

        if (localUploadPath.isEmpty()){
            throw new JsonCustomException(ErrCode.NULL_POINT_ERROR, "localUploadPath");
        }

        return localUploadPath;
    }


    /**
     * JSON 데이터를 플랫 파일로 변환합니다.
     *
     * @param recordResponses JSON 데이터 객체 리스트
     * @param localUploadPath 로컬 저장 경로
     * @param fileName        생성된 파일명
     * @throws FtpCustomException 파일 생성 중 오류 발생 시
     */
    private void convertToFlatFile(List<RecordResponse> recordResponses, String localUploadPath, String fileName) throws FtpCustomException {

        jsonService.convertToFlatFile(recordResponses, localUploadPath, fileName);

    }

    /**
     * 파일을 FTP 서버에 업로드합니다.
     *
     * @param ftpConfig      FTP 연결 정보
     * @param localFilePath  로컬 파일 경로
     * @param fileName       업로드할 파일명
     * @return 업로드 성공 여부
     * @throws FtpCustomException FTP 업로드 중 오류 발생 시
     */
    private boolean uploadFileToFtp(Map<String, String> ftpConfig, String localFilePath, String fileName) throws FtpCustomException {

        boolean isUploaded = jsonService.uploadFileToFtp(ftpConfig, localFilePath, fileName);

        if (!isUploaded){
            throw new FtpCustomException(ErrCode.FTP_UPLOAD_FAILED);
        }

        return isUploaded;
    }
}
