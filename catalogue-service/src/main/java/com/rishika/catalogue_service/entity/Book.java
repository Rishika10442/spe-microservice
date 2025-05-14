package com.rishika.catalogue_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String authors; // You can parse/store as CSV if multiple

    private String imageUrl;

    private Double rating;

    private Integer ratingCount;

    private Integer reviewCount;
    private Integer price;

    private String pages; // Could be changed to Integer, but you had "374 pages" format

    @Column(length = 1000)
    private String genres; // Store as CSV like: "Fantasy,Young Adult,..."

    private String isbn;
}
