package com.inspien.json.service;

import com.inspien.common.exception.FtpCustomException;
import com.inspien.common.exception.JsonCustomException;
import com.inspien.common.exception.SoapCustomException;
import com.inspien.json.dto.RecordResponse;
import com.inspien.soap.dto.User;

import java.util.List;
import java.util.Map;

/**
 * JSON 데이터를 처리하고 FTP 업로드 관련 작업을 수행하는 서비스 인터페이스.
 */
public interface JsonService {

    /**
     * JSON 데이터를 특정 태그 기반으로 파싱하여 객체 리스트로 변환합니다.
     *
     * @param jsonData JSON 데이터 (문자열 형태)
     * @param tagName  추출할 태그 이름
     * @param tClass   변환할 클래스 타입
     * @param <T>      변환될 객체 타입
     * @return 변환된 객체 리스트
     * @throws JsonCustomException JSON 데이터 파싱 중 오류가 발생한 경우
     */
    <T> List<T> jsonDataToList(String jsonData, String tagName, Class<T> tClass) throws JsonCustomException;

    /**
     * 로컬 파일 업로드 경로를 로드합니다.
     *
     * @return 로컬 업로드 경로
     * @throws JsonCustomException 업로드 경로를 찾을 수 없거나 읽는 중 오류가 발생한 경우
     */
    String loadLocalUploadPath() throws JsonCustomException;

    /**
     * RecordResponse 리스트를 플랫 파일로 변환하여 저장합니다.
     *
     * @param recordResponses 플랫 파일로 변환할 데이터 리스트
     * @param localUploadPath 로컬 저장 경로
     * @param fileName        생성할 파일 이름
     * @throws FtpCustomException 파일 생성 중 오류가 발생한 경우
     */
    void convertToFlatFile(List<RecordResponse> recordResponses, String localUploadPath, String fileName) throws FtpCustomException;

    /**
     * 유저 정보를 기반으로 저장 파일 이름을 생성합니다.
     *
     * @param user 파일 이름 생성에 필요한 유저 정보
     * @return 생성된 파일 이름
     * @throws SoapCustomException 유저 정보가 유효하지 않은 경우
     */
    String createSaveFileName(User user) throws SoapCustomException;

    /**
     * FTP 서버에 파일을 업로드합니다.
     *
     * @param ftpConfig       FTP 연결 정보 (host, port, user, password, filepath)
     * @param localUploadPath 로컬 파일 경로
     * @param fileName        업로드할 파일 이름
     * @return 업로드 성공 여부
     * @throws FtpCustomException FTP 업로드 중 오류가 발생한 경우
     */
    boolean uploadFileToFtp(Map<String, String> ftpConfig, String localUploadPath, String fileName) throws FtpCustomException;
}
