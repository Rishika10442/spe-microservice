package com.rishika.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderItemDTO {
    private Long orderItemId;
    private Long bookId;
    private Integer quantity;
    private Double priceAtPurchase;
}