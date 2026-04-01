package com.library.management.repository;

import com.library.management.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByUuid(UUID uuid);
    Optional<Book> findByUuidAndDeletedFalse(UUID uuid);
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    boolean existsByUuid(UUID uuid);
    boolean existsByIsbnAndUuidNot(String isbn, UUID uuid);
    Page<Book> findByDeletedFalse(Pageable pageable);
}
