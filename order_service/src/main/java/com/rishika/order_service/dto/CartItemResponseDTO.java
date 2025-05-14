package com.rishika.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long bookId;
    private Long userId;
    private Integer quantity;
    private Double price;
    private LocalDateTime addedAt;
}
