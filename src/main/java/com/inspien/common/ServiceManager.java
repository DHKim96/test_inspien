package com.inspien.common;

import com.inspien.common.exception.AbstractProcessException;
import com.inspien.json.controller.JsonController;
import com.inspien.soap.controller.SoapController;
import com.inspien.soap.dto.SoapResponse;
import com.inspien.soap.dto.User;
import com.inspien.xml.controller.XmlController;
import lombok.extern.slf4j.Slf4j;

/**
 * 전체 애플리케이션의 주요 비즈니스 로직을 관리하는 클래스.
 * <p>
 * SOAP, XML, JSON 데이터를 처리하는 개별 컨트롤러를 호출하여
 * 전체 프로세스를 실행하고 관리합니다. 각 단계의 예외를 처리하며,
 * 로그를 통해 실행 결과를 기록합니다.
 * </p>
 *
 * <p>
 * 주요 역할:
 * <ul>
 *     <li>SOAP 프로세스 실행 및 SOAP 응답 처리</li>
 *     <li>XML 데이터 처리 및 결과 로그 출력</li>
 *     <li>JSON 데이터 처리 및 업로드 상태 확인</li>
 * </ul>
 * </p>
 *
 * <p>
 * 사용 클래스:
 * <ul>
 *     <li>{@link com.inspien.soap.controller.SoapController} - SOAP 데이터를 처리하는 컨트롤러</li>
 *     <li>{@link com.inspien.xml.controller.XmlController} - XML 데이터를 처리하는 컨트롤러</li>
 *     <li>{@link com.inspien.json.controller.JsonController} - JSON 데이터를 처리하는 컨트롤러</li>
 * </ul>
 * </p>
 */
@Slf4j
public class ServiceManager {

    private final SoapController soapController;
    private final XmlController xmlController;
    private final JsonController jsonController;

    /**
     * 기본 생성자.
     * <p>SOAP, XML, JSON 데이터를 처리하는 각 컨트롤러의 인스턴스를 초기화합니다.</p>
     */
    public ServiceManager() {
        soapController = new SoapController();
        xmlController = new XmlController();
        jsonController = new JsonController();
    }

    /**
     * 전체 프로세스를 실행하는 메서드.
     * <p>
     * SOAP 서비스 호출, XML 데이터 처리, JSON 데이터 처리 순으로 실행하며,
     * 각 단계의 결과를 로그로 기록합니다. 예외 발생 시 적절한 처리 로직을 수행합니다.
     * </p>
     *
     * @param user 사용자 정보 객체
     */
    public void execute(User user) {
        try {
            log.info("프로세스 시작");
            SoapResponse soapResponse = executeSoapProcess(user);
            int xmlProcessResult = executeXmlProcess(soapResponse);
            boolean isUploaded = executeJsonProcess(soapResponse, user);
            logProcessResult(xmlProcessResult, isUploaded);
        } catch (AbstractProcessException e) {
            handleProcessException(e);
        } catch (Exception e) {
            log.error("프로세스 실행 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * SOAP 프로세스를 실행하고 응답 데이터를 반환합니다.
     *
     * @param user 사용자 정보 객체
     * @return SOAP 응답 객체
     * @throws AbstractProcessException SOAP 처리 중 예외 발생 시
     */
    private SoapResponse executeSoapProcess(User user) throws AbstractProcessException {
        log.info("SOAP 프로세스 시작");
        SoapResponse response = soapController.executeService(user);
        log.info("SOAP 프로세스 완료");
        return response;
    }

    /**
     * XML 데이터를 처리하고 처리된 데이터 수를 반환합니다.
     *
     * @param soapResponse SOAP 응답 객체
     * @return 처리된 데이터 수
     * @throws AbstractProcessException XML 처리 중 예외 발생 시
     */
    private int executeXmlProcess(SoapResponse soapResponse) throws AbstractProcessException {
        log.info("XML 프로세스 시작");
        int result = xmlController.processXmlData(soapResponse);
        log.info("XML 프로세스 완료. 처리된 데이터 수: {}", result);
        return result;
    }

    /**
     * JSON 데이터를 처리하고 업로드 결과를 반환합니다.
     *
     * @param soapResponse SOAP 응답 객체
     * @param user         사용자 정보 객체
     * @return 업로드 성공 여부
     * @throws AbstractProcessException JSON 처리 중 예외 발생 시
     */
    private boolean executeJsonProcess(SoapResponse soapResponse, User user) throws AbstractProcessException {
        log.info("JSON 프로세스 시작");
        boolean isUploaded = jsonController.processJsonData(soapResponse, user);
        log.info("JSON 프로세스 완료. 업로드 결과: {}", isUploaded ? "성공" : "실패");
        return isUploaded;
    }

    /**
     * 프로세스 결과를 로그로 기록합니다.
     *
     * @param xmlProcessResult 처리된 XML 데이터 수
     * @param isUploaded       JSON 업로드 성공 여부
     */
    private void logProcessResult(int xmlProcessResult, boolean isUploaded) {
        log.info("전체 프로세스 완료. XML 처리 데이터 수: {}, JSON 업로드 결과: {}", xmlProcessResult, isUploaded ? "성공" : "실패");
    }

    /**
     * 프로세스 실행 중 발생한 {@link AbstractProcessException}을 처리합니다.
     *
     * @param e 처리할 예외 객체
     */
    private void handleProcessException(AbstractProcessException e) {
        String errorType = e.getClass().getSimpleName().replace("CustomException", "");
        log.error("{} 프로세스 실행 중 오류 발생: {}", errorType, e.getMessage(), e);
    }
}
