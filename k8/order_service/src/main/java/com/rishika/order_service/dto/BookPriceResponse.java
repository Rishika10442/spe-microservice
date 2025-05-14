package com.rishika.order_service.dto;


import lombok.Data;

@Data
public class BookPriceResponse {
    private boolean success;
    private Double price;
}
