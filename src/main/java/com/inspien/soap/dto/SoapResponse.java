package com.inspien.soap.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SoapResponse {
    String xmlData;
    String jsonData;
    String dbConnInfo;
    String ftpConnInfo;
}
