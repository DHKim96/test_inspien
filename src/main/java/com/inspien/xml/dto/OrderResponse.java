package com.inspien.xml.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 주문의 헤더 정보를 저장하는 DTO 클래스.
 * <p>
 * 주요 필드:
 * <ul>
 *     <li>orderNum - 주문 번호</li>
 *     <li>orderId - 주문 ID</li>
 *     <li>orderDate - 주문 날짜</li>
 *     <li>orderPrice - 주문 총 금액</li>
 *     <li>orderQty - 주문 수량</li>
 *     <li>receiverName - 수령자 이름</li>
 *     <li>receiverNo - 수령자 연락처</li>
 *     <li>etaDate - 예상 배송 날짜</li>
 *     <li>destination - 배송지 주소</li>
 *     <li>description - 주문 설명</li>
 * </ul>
 */
@Data
@Builder
public class OrderResponse {
    int orderNum;
    String orderId;
    String orderDate;
    int orderPrice;
    int orderQty;
    String receiverName;
    String receiverNo;
    String etaDate;
    String destination;
    String description;
}
