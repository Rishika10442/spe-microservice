package com.rishika.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderResponseDTO {
    private Long orderId;
    private LocalDateTime orderedAt;
    private Double totalAmount;
    private String status;
    private List<UserOrderItemDTO> items;
}
