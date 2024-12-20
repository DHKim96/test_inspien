package com.inspien.common.util;


import com.inspien.common.exception.FtpCustomException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * FTP 서버와의 연결 및 파일 전송을 관리하는 유틸리티 클래스.
 * <p>
 * 주요 역할:
 * <ul>
 *     <li>FTP 서버 연결 및 인증</li>
 *     <li>로컬 파일을 FTP 서버로 업로드</li>
 *     <li>FTP 연결 종료</li>
 * </ul>
 */
public class FtpClientUtil {
    private final String type;
    private final FTPClient ftpClient;
    private static final String ENCODING = "UTF-8";


    /**
     * FTPClientUtil 생성자.
     * <p>
     * FTP 서버와 연결하고 로그인 절차를 수행합니다.
     *
     * @param host     FTP 서버 호스트
     * @param port     FTP 서버 포트
     * @param username FTP 사용자 이름
     * @param password FTP 사용자 비밀번호
     * @throws FtpCustomException FTP 연결 또는 인증 실패 시 발생
     */
    public FtpClientUtil(String host, int port, String username, String password) throws FtpCustomException {
        type = "FTP";
        this.ftpClient = new FTPClient();
        try {
            ftpClient.setControlEncoding(ENCODING); // 인코딩 설정(connect 이전으로 위치 수정 시 성공)
            ftpClient.connect(host, port);

            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new FtpCustomException(ErrCode.CONNECTION_FAILED, type);
            }

            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setConnectTimeout(10000); // 연결 타임아웃 10초 설정
            ftpClient.setSoTimeout(5000); // 데이터 전송 타임아웃 5초 설정
        } catch (IOException e) {
            throw new FtpCustomException(ErrCode.CONNECTION_LOGIN_FAILED, type, e);
        }
    }


    /**
     * 로컬 파일을 FTP 서버에 업로드합니다.
     *
     * @param localPath         로컬 파일 경로
     * @param remoteUploadPath  FTP 서버 업로드 경로
     * @return 업로드 성공 여부
     * @throws FtpCustomException 파일 업로드 실패 또는 IO 오류 시 발생
     */
    public boolean uploadFile(String localPath, String remoteUploadPath) throws FtpCustomException {
        boolean result = false;

        try(FileInputStream input = new FileInputStream(localPath)) {

            result = ftpClient.storeFile(remoteUploadPath, input);
        } catch (IOException e) {
            throw new FtpCustomException(ErrCode.FILE_NOT_READ, localPath, e);
        }

        return result;
    }


    /**
     * FTP 연결을 종료합니다.
     * <p>
     * 로그아웃 절차를 수행한 후 연결을 끊습니다.
     *
     * @throws FtpCustomException 연결 해제 실패 시 발생
     */
    public void disconnect() throws FtpCustomException {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new FtpCustomException(ErrCode.CONNECTION_DISCONNECT_FAILED, type, e);
            }
        }
    }

}
