package com.rishika.borrow_service.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String coverImageUrl;
    private String authors;
    private String description;
    private Double rating;
    private Integer ratingCount;
    private Integer reviewCount;
    private String pageCount;
    private String genres;
    private String isbn;
}