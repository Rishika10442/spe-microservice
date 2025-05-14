package com.rishika.borrow_service.dto;


import com.rishika.borrow_service.entity.Borrow;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowWithBookDTO {
    private Borrow borrow;
    private BookDTO book;
}
