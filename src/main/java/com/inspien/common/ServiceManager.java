package com.inspien.common;

import com.inspien.common.exception.AbstractProcessException;
import com.inspien.common.exception.JsonDataProcessException;
import com.inspien.common.exception.SoapProcessException;
import com.inspien.common.exception.XmlDataProcessException;
import com.inspien.json.controller.JsonController;
import com.inspien.soap.controller.SoapController;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import com.inspien.xml.controller.XmlController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
            log.info("SOAP 서비스 호출 시작");
            SoapResponse soapResponse = soapController.executeService(user);
            log.info("SOAP 서비스 호출 완료");

            // 2. XML 데이터 처리
            log.info("XML 데이터 처리 시작");
            int xmlProcessResult = xmlController.processXmlData(soapResponse);
            log.info("XML 데이터 처리 완료. {}건 처리됨", xmlProcessResult);

            // 3. JSON 데이터 처리
            log.info("JSON 데이터 처리 시작");
            boolean isUploaded = jsonController.processJsonData(soapResponse, user);
            String res = isUploaded ? "성공" : "실패";
            log.info("JSON 데이터 처리 완료. 업로드 {}", res);

            log.info("전체 프로세스가 성공적으로 완료되었습니다.");
        } catch (SoapProcessException e) {
            logError("SOAP", e);
        } catch (XmlDataProcessException e) {
            logError("XML", e);
        } catch (JsonDataProcessException e) {
            logError("JSON", e);
        } catch (Exception e) {
            log.error("프로세스 실행 중 알 수 없는 오류가 발생했습니다. {}", e.getMessage(), e);
        }
    }

    private void logError(String processName, Exception e) {
        log.error("{} 프로세스 실행 중 오류가 발생했습니다. {}", processName, e.getMessage(), e);
    }
}
