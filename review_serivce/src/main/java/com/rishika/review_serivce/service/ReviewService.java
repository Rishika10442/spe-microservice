package com.rishika.review_serivce.service;

import com.rishika.review_serivce.entity.Review;
import com.rishika.review_serivce.repo.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> addReview(Long userId, Long bookId, String reviewText, Integer rating,String title, String jwtToken) {
        String USER_SERVICE_URL = "http://user-service:8000/user/";
         String BOOK_SERVICE_URL = "http://catalogue-service:8082/catalogue/booksById/";
//        String USER_SERVICE_URL = "http://localhost:8081/user/";
//        String BOOK_SERVICE_URL = "http://localhost:8081//catalogue/booksById/";
        Map<String, Object> response = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // 1. Validate User
            ResponseEntity<String> userResponse = restTemplate.exchange(
                    USER_SERVICE_URL + userId, HttpMethod.GET, entity, String.class);
            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            // 2. Validate Book
            ResponseEntity<String> bookResponse = restTemplate.exchange(
                    BOOK_SERVICE_URL + bookId, HttpMethod.GET, entity, String.class);
            if (!bookResponse.getStatusCode().is2xxSuccessful()) {
                response.put("success", false);
                response.put("message", "Book not found");
                return response;
            }

            // 3. Save Review
            Review review = Review.builder()
                    .userId(userId)
                    .bookId(bookId)
                    .review(reviewText)
                    .rating(rating)
                    .title(title)
                    .timestamp(LocalDateTime.now())
                    .updatedTimestamp(LocalDateTime.now())
                    .build();

            reviewRepository.save(review);

            response.put("success", true);
            response.put("message", "Review added successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> updateReview(Long reviewId, String title, String updatedReview, Integer updatedRating) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Review> optionalReview = reviewRepository.findById(reviewId);

            if (optionalReview.isEmpty()) {
                response.put("success", false);
                response.put("message", "Review not found with ID: " + reviewId);
                return response;
            }

            Review review = optionalReview.get();

            if (title != null) review.setTitle(title);
            if (updatedReview != null) review.setReview(updatedReview);
            if (updatedRating != null) review.setRating(updatedRating);

            review.setUpdatedTimestamp(LocalDateTime.now());

            reviewRepository.save(review);

            response.put("success", true);
            response.put("message", "Review updated successfully");
            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating review: " + e.getMessage());
            return response;
        }
    }

    public Map<String, Object> getReviewsByBookId(Long bookId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Review> reviews = reviewRepository.findByBookId(bookId);

            response.put("success", true);
            response.put("reviews", reviews);
            return response;

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving reviews: " + e.getMessage());
            return response;
        }
    }


}
