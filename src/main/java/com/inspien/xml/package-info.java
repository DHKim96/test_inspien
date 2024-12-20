/**
 * 이 패키지는 SOAP Response의 XML 데이터를 처리하고 DB 작업을 수행하는 기능을 제공합니다.
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>XML 데이터를 파싱하여 DTO 객체로 변환</li>
 *     <li>파싱된 데이터를 기반으로 DB 작업 수행</li>
 *     <li>XML 데이터 내 특정 태그(Header, Detail) 처리</li>
 * </ul>
 * <p>
 * 주요 클래스:
 * <ul>
 *     <li>{@link com.inspien.xml.service.XmlService} - XML 데이터 처리 인터페이스</li>
 *     <li>{@link com.inspien.xml.service.XmlServiceImpl} - XmlService 구현체</li>
 *     <li>{@link com.inspien.xml.dto.OrderInsert} - 파싱된 Order 데이터를 포함하는 DTO 클래스</li>
 *     <li>{@link com.inspien.xml.dto.ItemResponse} - Order의 상세 정보를 담는 DTO 클래스</li>
 *     <li>{@link com.inspien.xml.dto.OrderResponse} - Order의 헤더 정보를 담는 DTO 클래스</li>
 * </ul>
 */
package com.inspien.xml;
