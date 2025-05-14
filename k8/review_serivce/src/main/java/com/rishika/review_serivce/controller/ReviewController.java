package com.rishika.review_serivce.controller;


import com.rishika.review_serivce.dto.ReviewRequest;
import com.rishika.review_serivce.dto.UpdateReviewRequest;
import com.rishika.review_serivce.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    @Autowired
    private ReviewService reviewService;
    @GetMapping("/test")
    public String test() {
        return "review Service is Working!";
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestHeader("Authorization") String jwtToken,
                                       @RequestBody ReviewRequest reviewRequest) {
        try {
            Map<String, Object> response = reviewService.addReview(
                    reviewRequest.userId(),
                    reviewRequest.bookId(),
                    reviewRequest.review(),
                    reviewRequest.rating(),
                    reviewRequest.title(),
                    jwtToken
            );
            if ((boolean) response.get("success")) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateReview(@RequestBody @Valid UpdateReviewRequest request) {
        try {
            Map<String, Object> response = reviewService.updateReview(
                    request.reviewId(),
                    request.title(),
                    request.review(),
                    request.rating()
            );

            if ((boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getReviewsForBook(@PathVariable Long bookId) {
        try {
            logger.info("bookId {}",bookId);
            Map<String, Object> response = reviewService.getReviewsByBookId(bookId);

            if ((boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "An error occurred: " + e.getMessage()));
        }
    }
}
