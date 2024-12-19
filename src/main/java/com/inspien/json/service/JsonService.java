package com.inspien.json.service;

import com.inspien.json.dto.RecordResponse;
import com.inspien.soap.dto.User;

import java.util.List;
import java.util.Map;

public interface JsonService {
    <T> List<T> jsonDataToList(String jsonData, String tagName, Class<T> tClass);

    String loadLocalUploadPath();

    void convertToFlatFile(List<RecordResponse> recordResponses, String localUploadPath, String fileName);

    String createSaveFileName(User user);

    boolean uploadFileToFtp(Map<String, String> ftpconfig, String localUploadPath, String fileName);
}
