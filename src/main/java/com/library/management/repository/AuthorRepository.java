package com.library.management.repository;

import com.library.management.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByUuid(UUID uuid);
    Optional<Author> findByUuidAndDeletedFalse(UUID uuid);
    Page<Author> findByDeletedFalse(Pageable pageable);
    boolean existsByUuid(UUID uuid);
}

