package com.inspien.common;

import com.inspien.json.controller.JsonController;
import com.inspien.soap.controller.SoapController;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import com.inspien.xml.controller.XmlController;

public class ServiceManager {
    private final SoapController soapController;
    private final XmlController xmlController;
    private final JsonController jsonController;

    public ServiceManager() {
        soapController = new SoapController();
        xmlController = new XmlController();
        jsonController = new JsonController();
    }

    public void execute(User user) {
        try {
            // 1. SOAP 서비스 호출 및 응답
            SoapResponse soapResponse = soapController.executeService(user);

            // 2. XML 데이터 처리
            xmlController.processXmlData(soapResponse);

            // 3. JSON 데이터 처리
            jsonController.processJsonData(soapResponse, user);

            System.out.println("전체 프로세스가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            System.err.println("프로세스 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
