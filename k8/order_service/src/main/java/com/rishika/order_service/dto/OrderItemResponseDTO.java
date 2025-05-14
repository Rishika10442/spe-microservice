package com.rishika.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long orderItemId;
    private Long bookId;
    private Integer quantity;
    private Double priceAtPurchase;
}