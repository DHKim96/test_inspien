package com.inspien.util;


import com.inspien.exception.FtpClientException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

public class FtpClientUtil {

    private final FTPClient ftpClient;
    public FtpClientUtil(String host, int port, String username, String password) {
        this.ftpClient = new FTPClient();
        try {
            ftpClient.setControlEncoding("UTF-8"); // 인코딩 설정(connect 이전으로 위치 수정 시 성공)
            ftpClient.connect(host, port);

            int reply = ftpClient.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new FtpClientException("ftp 서버 연결에 실패했습니다.");
            }
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.setConnectTimeout(10000); // 연결 타임아웃 10초 설정
            ftpClient.setSoTimeout(5000); // 데이터 전송 타임아웃 5초 설정
        } catch (IOException e) {
            throw new FtpClientException("ftp 서버 로그인에 실패했습니다.");
        }
    }

    public boolean uploadFile(String localPath, String remoteUploadPath) {
        boolean result = false;

        try(FileInputStream input = new FileInputStream(localPath)) {

            result = ftpClient.storeFile(remoteUploadPath, input);
        } catch (IOException e) {
            throw new FtpClientException("파일을 읽어오는 데 실패했습니다.", e);
        }

        return result;
    }

    public void disconnect() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new FtpClientException("ftpClient 해제 실패", e);
            }
        }
    }

}
