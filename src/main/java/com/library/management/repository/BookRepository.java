package com.library.management.repository;

import com.library.management.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    @EntityGraph(attributePaths = {"authors"})
    Optional<Book> findById(UUID uuid);

    @EntityGraph(attributePaths = {"authors"})
    Optional<Book> findByIdAndDeletedFalse(UUID uuid);
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbnAndDeletedFalse(String isbn);
    boolean existsByIsbn(String isbn);
    boolean existsById(UUID uuid);
    boolean existsByIsbnAndIdNot(String isbn, UUID id);

    @EntityGraph(attributePaths = {"authors"})
    Page<Book> findByDeletedFalse(Pageable pageable);
}
