/**
 * 애플리케이션 전반에서 발생하는 다양한 예외를 처리하는 커스텀 예외 클래스들을 포함하는 패키지입니다.
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>비즈니스 로직 실행 중 발생하는 예외를 통합적으로 처리</li>
 *     <li>에러 코드와 메시지를 기반으로 한 예외 클래스 제공</li>
 *     <li>예외 원인을 명확히 파악하고 로그로 출력</li>
 * </ul>
 * <p>
 * 주요 클래스:
 * <ul>
 *     <li>{@link com.inspien.common.exception.AbstractProcessException} - 모든 커스텀 예외의 최상위 추상 클래스</li>
 *     <li>{@link com.inspien.common.exception.DbCustomException} - 데이터베이스 관련 예외 처리 클래스</li>
 *     <li>{@link com.inspien.common.exception.FtpCustomException} - FTP 작업 관련 예외 처리 클래스</li>
 *     <li>{@link com.inspien.common.exception.JsonCustomException} - JSON 데이터 처리 관련 예외 처리 클래스</li>
 *     <li>{@link com.inspien.common.exception.ParseCustomException} - 데이터 파싱 관련 예외 처리 클래스</li>
 *     <li>{@link com.inspien.common.exception.SoapCustomException} - SOAP 서비스 관련 예외 처리 클래스</li>
 *     <li>{@link com.inspien.common.exception.XmlCustomException} - XML 데이터 처리 관련 예외 처리 클래스</li>
 * </ul>
 */
package com.inspien.common.exception;
