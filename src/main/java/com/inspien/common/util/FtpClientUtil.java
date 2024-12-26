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
        this.ftpClient = new FTPClient(); // 인스턴스 생성
        try {
            ftpClient.setControlEncoding(ENCODING); // 인코딩 설정(connect 이전으로 위치 수정 시 성공)
            // FTPClient의 제어 연결(control connection)에 대한 인코딩 설정이 서버와의 연결이 이루어지기 전에 이루어져야 하기 때문

            ftpClient.connect(host, port); //  3-way Handshake 방식으로 연결을 수행

            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new FtpCustomException(ErrCode.CONNECTION_FAILED, type);
            }

            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode(); // FTP 클라이언트를 패시브 모드로 작동
            /*
                * 액티브 모드
                *   1. 클라이언트가 먼저 서버에 연결 요청.
                *   2. 서버가 클라이언트의 데이터 포트로 되돌아 연결.
                *   => 클라이언트가 방화벽 뒤에 있을 경우 연결 문제 발생 가능
                *
                * 패시브 모드
                *   1. 클라이언트가 서버에 데이터 포트 연결 요청
                *   2. 서버가 데이터 전송용 포트 번호를 클라이언트에 알려줌
                *   3. 클라이언트가 해당 포트로 연결 시도
                *   => 연결 문제 회피 가능
                *
             */

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
