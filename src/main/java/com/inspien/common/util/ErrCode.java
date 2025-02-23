package com.inspien.common.util;

/**
 * 시스템에서 발생할 수 있는 다양한 에러 코드와 메시지를 정의하는 열거형 클래스.
 * <p>
 * 주요 역할:
 * <ul>
 *     <li>각 에러 코드와 대응되는 메시지 관리</li>
 *     <li>에러 식별을 위한 고유 코드 제공</li>
 * </ul>
 */
public enum ErrCode {

    // 형식 관련 에러
    INVALID_FORMAT("E001", "%s 형식이 유효하지 않습니다."),

    // 파일 관련 에러
    FILE_NOT_FOUND("E002", "%s 파일이 해당 경로에 존재하지 않습니다."),
    PROPERTY_NOT_FOUND("E003", "%s 내에 %s property 가 존재하지 않습니다."),
    FILE_NOT_READ("E004", "%s 파일을 읽는 중 오류가 발생했습니다."),
    FILE_CREATE_FAILED("E021", "파일 생성 중 오류가 발생했습니다."),

    // Null 또는 유효성 관련 에러
    NULL_POINT_ERROR("E005", "%s가 비어 있습니다."),

    // 연결 관련 에러
    CONNECTION_NOT_CREATED("E006", "%s 커넥션 생성 중 오류가 발생했습니다."),
    CONNECTION_FAILED("E007", "%s 연결 중 오류가 발생했습니다."),
    CONNECTION_TYPE_UNSUPPORTED("E023", "%s 은(는) 지원하지 않는 연결 타입입니다."),
    CONNECTION_LOGIN_FAILED("E024", "%s 서버 로그인에 실패했습니다."),
    CONNECTION_DISCONNECT_FAILED("E025", "%s 연결 해제 중 오류가 발생했습니다."),

    // SOAP 관련 에러
    SOAP_NOT_CREATED("E008", "%s 객체 생성 중 오류가 발생했습니다."),
    SOAP_MESSAGE_OUTPUT_ERROR("E009", "SOAPMessage를 OutputStream에 작성 중 오류가 발생했습니다."),
    SOAP_MESSAGE_TO_STRING_ERROR("E010", "SOAPMessage OutputStream을 String으로 변환 중 오류가 발생했습니다."),
    SOAP_DOCUMENT_BUILDER_NOT_CREATED("E011", "DocumentBuilder 생성 중 오류가 발생했습니다."),

    // 스트림 관련 에러
    IO_STREAM_ERROR("E012", "입출력 오류가 발생했습니다."),

    // 인코딩 관련 에러
    ENCODING_NOT_SUPPORTED("E014", "%s 은(는) 지원하지 않는 인코딩 형식입니다."),

    // XML 관련 에러
    XML_ELEMENT_NOT_FOUND("E015", "XML 데이터에서 %s가 존재하지 않습니다."),
    XML_CHILD_NODE_LESS("E016", "%s 노드의 자식 노드가 예상보다 적습니다."),

    // 데이터베이스 관련 에러
    DATABASE_INSERT_FAILED("E017", "%s 테이블에 %s 데이터 INSERT 시 오류가 발생했습니다."),
    DATABASE_COMMIT_FAILED("E027", "COMMIT 중 오류가 발생했습니다."),
    DATABASE_ROLLBACK_FAILED("E027", "ROLLBACK 중 오류가 발생했습니다."),
    DATABASE_AUTO_COMMIT_ERROR("E028", "AUTOCOMMIT 설정 중 오류가 발생했습니다."),

    // JSON 관련 에러
    JSON_FIELD_NOT_FOUND("E018", "JSON 데이터에서 %s 값이 존재하지 않습니다."),
    JSON_NOT_ARRAY("E019", "JSON 데이터에서 %s 값이 배열 형태가 아닙니다."),
    JSON_NOT_MAPPED("E020", "JSON 데이터를 %s 객체로 역직렬화 중 오류가 발생했습니다."),

    // FTP 관련 에러
    FTP_UPLOAD_FAILED("E022", "FTP 서버에 파일 업로드 중 오류가 발생했습니다."),

    // 클래스 관련 에러
    CLASS_NOT_FOUND("E026", "%s 클래스를 찾을 수 없습니다."),

    // 기타
    UNKNOWN_ERROR("E999", "알 수 없는 에러가 발생했습니다.");

    private final String code;
    private final String msg;

    /**
     * 에러 코드 및 메시지를 초기화합니다.
     *
     * @param code 에러 코드
     * @param msg  에러 메시지
     */
    ErrCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 에러 코드를 반환합니다.
     *
     * @return 에러 코드
     */
    public String getCode() {
        return code;
    }

    /**
     * 에러 메시지를 반환합니다.
     *
     * @return 에러 메시지
     */
    public String getMsg() {
        return msg;
    }
}
