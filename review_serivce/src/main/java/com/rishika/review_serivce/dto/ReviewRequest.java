package com.rishika.review_serivce.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;


public record ReviewRequest(
        @NotNull(message = "User ID is required")
        @JsonProperty("userId")
        Long userId,

        @NotNull(message = "Book ID is required")
        @JsonProperty("bookId")
        Long bookId,

        @NotNull(message = "Review title is required")
        @Size(max = 100, message = "Title can't be more than 100 characters")
        @JsonProperty("title")
        String title,

        @NotNull(message = "Review text is required")
        @Size(max = 500, message = "Review text can't be more than 500 characters")
        @JsonProperty("review")
        String review,

        @NotNull(message = "Rating is required")
        @JsonProperty("rating")
        Integer rating
) {
}