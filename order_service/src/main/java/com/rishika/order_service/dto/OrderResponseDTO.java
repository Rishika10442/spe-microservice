package com.rishika.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private Long userId;
    private Double totalAmount;
    private LocalDateTime orderedAt;
    private String status;
    private List<OrderItemResponseDTO> orderItems;
}
