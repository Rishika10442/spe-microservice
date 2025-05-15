package com.rishika.catalogue_service.controller;

import com.rishika.catalogue_service.dto.BookDTO;
import com.rishika.catalogue_service.entity.Book;
import com.rishika.catalogue_service.mapper.BookMapper;
import com.rishika.catalogue_service.repo.BookRepository;
import com.rishika.catalogue_service.response.ApiResponse;
import com.rishika.catalogue_service.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/catalogue")
public class CatalogueController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;
    @GetMapping("/test")
    public String test() {
        return "Catalogue Service is Working!";
    }


    @GetMapping("/books")
    public ResponseEntity<ApiResponse> getBooks(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size){
        try {
            // Fetch books with pagination
            var books = bookService.getBooksByPage(page, size);

            // Create the API response with success message and data
            ApiResponse response = ApiResponse.<Object>builder()
                    .success(true)
                    .message("Books fetched successfully")
                    .data(books)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // If there is an error, return a failure response
            ApiResponse response = ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to fetch books: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(500).body(response); // Return 500 on error
        }
    }

    @GetMapping("/fetch")
    public ApiResponse<Book> getBookById(@RequestParam long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/search")
    public ApiResponse<List<BookDTO>> searchBooksByName(@RequestParam String name) {
        return bookService.searchBooksByName(name);
    }

    @GetMapping("/booksById/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }

    @PostMapping("/fetch-multiple")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getBooksByIds(@RequestBody List<Long> ids) {
        try {
            List<Book> books = bookRepository.findAllById(ids);

            List<BookDTO> bookDTOs = books.stream()
                    .map(BookMapper::toDTO)
                    .collect(Collectors.toList());

            ApiResponse<List<BookDTO>> response = ApiResponse.<List<BookDTO>>builder()
                    .success(true)
                    .message("Books fetched successfully")
                    .data(bookDTOs)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<List<BookDTO>> errorResponse = ApiResponse.<List<BookDTO>>builder()
                    .success(false)
                    .message("Internal error: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
//
//    @GetMapping("/price/{bookId}")
//    public ResponseEntity<?> getBookPrice(@PathVariable Long bookId) {
//        try {
//            Optional<Book> optionalBook = bookRepository.findById(bookId);
//            if (optionalBook.isPresent()) {
//                Integer price = optionalBook.get().getPrice();
//                return ResponseEntity.ok(Map.of(
//                        "success", true,
//                        "price", price
//                ));
//            } else {
//                return ResponseEntity.status(404).body(Map.of(
//                        "success", false,
//                        "message", "Book not found"
//                ));
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Map.of(
//                    "success", false,
//                    "message", "Error fetching price: " + e.getMessage()
//            ));
//        }
//    }

    @GetMapping("/price/{bookId}")
    public ResponseEntity<?> getBookPrice(@PathVariable Long bookId) {
        try {
            Optional<Book> optionalBook = bookRepository.findById(bookId);
            if (optionalBook.isPresent()) {
                Book book = optionalBook.get();
                Integer price = book.getPrice();
                String name = book.getTitle(); // assuming `name` is the field for book name
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "price", price,
                        "name", name
                ));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Book not found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error fetching price: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/book-details/{bookId}")
    public ResponseEntity<?> getBookDetails(@PathVariable Long bookId) {
        try {
            Optional<Book> optionalBook = bookRepository.findById(bookId);
            if (optionalBook.isPresent()) {
                Book book = optionalBook.get();
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "price", book.getPrice(),
                        "name", book.getTitle()
                ));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Book not found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error fetching book details: " + e.getMessage()
            ));
        }
    }


}
