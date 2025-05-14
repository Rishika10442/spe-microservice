package com.rishika.borrow_service.service;

import com.rishika.borrow_service.dto.BookDTO;
import com.rishika.borrow_service.dto.BorrowWithBookDTO;
import com.rishika.borrow_service.entity.Borrow;
import com.rishika.borrow_service.repo.BorrowRepository;
import com.rishika.borrow_service.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private RestTemplate restTemplate;

    // These will go through API Gateway
    private final String USER_SERVICE_URL = "http://localhost:8081/user/";
    private final String BOOK_SERVICE_URL = "http://localhost:8081/catalogue/booksById/";

    public Map<String, Object> addBorrow(Long userId, Long bookId, String jwtToken) {
        Map<String, Object> response = new HashMap<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            System.out.println("Calling: " + USER_SERVICE_URL + userId);

            // 1. Validate User
            ResponseEntity<String> userResponse = restTemplate.exchange(
                    USER_SERVICE_URL + userId, HttpMethod.GET, entity, String.class);


            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            System.out.println("Calling: " + BOOK_SERVICE_URL + bookId);

            // 2. Validate Book
            ResponseEntity<String> bookResponse = restTemplate.exchange(
                    BOOK_SERVICE_URL + bookId, HttpMethod.GET, entity, String.class);

            if (!bookResponse.getStatusCode().is2xxSuccessful()) {
                response.put("success", false);
                response.put("message", "Book not found");
                return response;
            }

            // 3. Create Borrow Record
            Borrow borrow = new Borrow();
            borrow.setUserId(userId);
            borrow.setBookId(bookId);
            LocalDate today = LocalDate.now();

            borrow.setIssueDate(today);
            borrow.setDueDate(today.plusDays(15));
            borrow.setIsReturned(false);
            borrow.setFineAmount(0.0);


            borrowRepository.save(borrow);

            response.put("success", true);
            response.put("borrowId", borrow.getBorrowId());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
        }
        return response;
    }

    public Map<String, Object> getBorrowsByUser(Long userId, String jwtToken) {
        Map<String, Object> response = new HashMap<>();
        String BOOK_URL = "http://localhost:8081/catalogue/fetch-multiple";
        try {
            List<Borrow> borrows = borrowRepository.findByUserId(userId);
            borrows.sort(Comparator.comparing(Borrow::getIsReturned));
            List<Long> bookIds = borrows.stream().map(Borrow::getBookId).distinct().collect(Collectors.toList());

            // Prepare headers with JWT
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", jwtToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<Long>> requestEntity = new HttpEntity<>(bookIds, headers);

            // Define response type
            ParameterizedTypeReference<ApiResponse<List<BookDTO>>> typeRef =
                    new ParameterizedTypeReference<>() {};

            ResponseEntity<ApiResponse<List<BookDTO>>> bookResponse = restTemplate.exchange(
                    BOOK_URL,
                    HttpMethod.POST,
                    requestEntity,
                    typeRef
            );

            // Map books by ID
            Map<Long, BookDTO> bookMap = new HashMap<>();
            if (bookResponse.getStatusCode().is2xxSuccessful()
                    && bookResponse.getBody() != null
                    && bookResponse.getBody().isSuccess()) {

                for (BookDTO book : bookResponse.getBody().getData()) {
                    bookMap.put(book.getId(), book);
                }
            }

            // Combine borrow + book
            List<BorrowWithBookDTO> borrowDetailsList = new ArrayList<>();
            for (Borrow borrow : borrows) {
                BorrowWithBookDTO dto = BorrowWithBookDTO.builder()
                        .borrow(borrow)
                        .book(bookMap.get(borrow.getBookId())) // may be null if not found
                        .build();
                borrowDetailsList.add(dto);
            }

            response.put("success", true);
            response.put("borrows", borrowDetailsList);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
        }
        return response;
    }


    public Map<String, Object> returnBook(Long userId, Long bookId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Find the borrow entry that is not yet returned
            Optional<Borrow> optionalBorrow = borrowRepository
                    .findByUserIdAndBookIdAndIsReturnedFalse(userId, bookId);

            if (optionalBorrow.isEmpty()) {
                response.put("success", false);
                response.put("message", "No active borrow record found for this user and book.");
                return response;
            }

            Borrow borrow = optionalBorrow.get();
            borrow.setReturnDate(LocalDate.now());
            borrow.setIsReturned(true);

            // You can add fine calculation logic here if needed

            borrowRepository.save(borrow);

            response.put("success", true);
            response.put("message", "Book returned successfully.");
            response.put("data", borrow);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Internal error: " + e.getMessage());
        }

        return response;
    }
}
