package com.rishika.catalogue_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Integer price;
}