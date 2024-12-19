package com.inspien.xml.dto;

import lombok.Builder;
import lombok.Data;

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
