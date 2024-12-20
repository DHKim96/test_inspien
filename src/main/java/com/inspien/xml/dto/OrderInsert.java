package com.inspien.xml.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 주문 데이터와 주문 상세 데이터를 포함하는 DTO 클래스.
 * <p>
 * 주요 필드:
 * <ul>
 *     <li>order - 주문의 헤더 정보 ({@link com.inspien.xml.dto.OrderResponse})</li>
 *     <li>items - 주문의 상세 정보 리스트 ({@link com.inspien.xml.dto.ItemResponse})</li>
 * </ul>
 */
@Data
@Builder
public class OrderInsert {
    OrderResponse order;
    List<ItemResponse> items;
}
