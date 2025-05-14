package com.rishika.catalogue_service.service;


import com.rishika.catalogue_service.dto.BookDTO;
import com.rishika.catalogue_service.entity.Book;
import com.rishika.catalogue_service.repo.BookRepository;
import com.rishika.catalogue_service.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public Page<BookDTO> getBooksByPage(int page, int size) {
        Page<Book> booksPage = bookRepository.findAll(PageRequest.of(page, size));
        return booksPage.map(this::convertToDTO);
    }

    private BookDTO convertToDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .coverImageUrl(book.getImageUrl())
                .authors(book.getAuthors())
                .description(book.getDescription())
                .rating(book.getRating())
                .ratingCount(book.getRatingCount())
                .reviewCount(book.getReviewCount())
                .pageCount(book.getPages())
                .genres(book.getGenres())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .build();
    }


    public ApiResponse<Book> getBookById(long id) {
        try {
            Book book = bookRepository.findById(id);  // Find book by ID

            if (book == null) {
                // Book not found, return failure response
                return new ApiResponse<>(false, "Book not found", null);
            }

            // Book found, return success response
            return new ApiResponse<>(true, "Book fetched successfully", book);

        } catch (Exception e) {
            // Exception handling - return error response
            return new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null);
        }
    }



    public ApiResponse<List<BookDTO>> searchBooksByName(String name) {
        try {
            // First, try to find an exact match
            List<Book> exactMatches = bookRepository.findByTitle(name);

            // If no exact matches, find most similar books
            if (exactMatches.isEmpty()) {
                List<Book> similarBooks = findMostSimilarBooks(name);
                return new ApiResponse<>(true, "No exact match found, but here are similar books", convertToDTOList(similarBooks));
            } else {
                return new ApiResponse<>(true, "Books found", convertToDTOList(exactMatches));
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error while searching books: " + e.getMessage(), null);
        }
    }

    // Convert list of Book entities to DTOs
    private List<BookDTO> convertToDTOList(List<Book> books) {
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to find similar books based on title
    private List<Book> findMostSimilarBooks(String name) {
        // Fuzzy search logic (simplified, you can enhance this further)
        List<Book> allBooks = bookRepository.findAll();
        return allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }


}
