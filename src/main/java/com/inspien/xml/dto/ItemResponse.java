package com.inspien.xml.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 주문 상세 정보를 저장하는 DTO 클래스.
 * <p>
 * 주요 필드:
 * <ul>
 *     <li>orderNum - 주문 번호</li>
 *     <li>itemSeq - 아이템 순번</li>
 *     <li>itemName - 아이템 이름</li>
 *     <li>itemQty - 아이템 수량</li>
 *     <li>itemColor - 아이템 색상</li>
 *     <li>itemPrice - 아이템 가격</li>
 * </ul>
 */
@Data
@Builder
public class ItemResponse {
    int orderNum;
    int itemSeq;
    String itemName;
    int itemQty;
    String itemColor;
    int itemPrice;
}
