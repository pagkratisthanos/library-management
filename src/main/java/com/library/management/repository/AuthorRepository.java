package com.library.management.repository;

import com.library.management.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {

    Optional<Author> findById(UUID uuid);
    Optional<Author> findByIdAndDeletedFalse(UUID uuid);
    Page<Author> findByDeletedFalse(Pageable pageable);
    boolean existsById(UUID uuid);
    boolean existsByLastname(String lastname);
    List<Author> findByBooks_Id(UUID bookUuid);
}

