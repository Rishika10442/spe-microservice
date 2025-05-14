package com.rishika.review_serivce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record UpdateReviewRequest(
        @NotNull(message = "Review ID is required")
        @JsonProperty("reviewId")
        Long reviewId,


        String title,
        String review,
        Integer rating
) {}