package com.inspien.util;


import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.IOException;

public class FtpClientUtil {

    private final FTPClient ftpClient;
    private final String filepath;

    public FtpClientUtil(String host, int port, String username, String password, String filepath) {
        this.ftpClient = new FTPClient();
        try {
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        } catch (IOException e) {
            throw new RuntimeException("ftpClient 연결 실패");
        }
        this.filepath = filepath;
    }

    public boolean uploadFile(String localPath) {
        boolean result = false;

        try(FileInputStream input = new FileInputStream(localPath)) {
            result = ftpClient.storeFile(filepath, input);
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽어오는 데 실패");
        }

        return result;
    }

    public void disconnect() {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("ftpClient 해제 실패");
            }
        }
    }

}
