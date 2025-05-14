package com.rishika.catalogue_service.mapper;

import com.rishika.catalogue_service.dto.BookDTO;
import com.rishika.catalogue_service.entity.Book;

public class BookMapper {
    public static BookDTO toDTO(Book book) {
        if (book == null) return null;

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .description(book.getDescription())
                .authors(book.getAuthors())
                .coverImageUrl(book.getImageUrl())
                .rating(book.getRating())
                .ratingCount(book.getRatingCount())
                .reviewCount(book.getReviewCount())
                .pageCount(book.getPages())
                .genres(book.getGenres())
                .isbn(book.getIsbn())
                .build();
    }
}
