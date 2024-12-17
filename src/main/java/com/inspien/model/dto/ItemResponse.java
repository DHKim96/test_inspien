package com.inspien.model.dto;

import lombok.Builder;
import lombok.Data;

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
