package com.rishika.catalogue_service.repo;

import com.rishika.catalogue_service.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);
    Book findById(long id);
    List<Book> findByTitle(String title);
    List<Book> findByIdIn(List<Long> ids);


    // You can add more advanced queries here for fuzzy matching or full-text search
    // For example, using LIKE for partial matches
    List<Book> findByTitleContainingIgnoreCase(String title);
}
