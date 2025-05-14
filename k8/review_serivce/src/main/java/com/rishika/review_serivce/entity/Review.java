package com.rishika.review_serivce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private Long userId;
    private Long bookId;


    @Column(nullable = false)
    private String review;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false)
    private LocalDateTime timestamp;


    private String title;

    private LocalDateTime updatedTimestamp;


}
