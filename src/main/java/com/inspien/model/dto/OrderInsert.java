package com.inspien.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderInsert {
    OrderResponse order;
    List<ItemResponse> items;
}
