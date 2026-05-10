package com.library.management.repository;

import com.library.management.model.Copy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CopyRepository extends JpaRepository<Copy, UUID> {

    Optional<Copy> findById(UUID uuid);
    Optional<Copy> findByIdAndDeletedFalse(UUID uuid);
    Page<Copy> findByDeletedFalse(Pageable pageable);
    boolean existsById(UUID uuid);
    List<Copy> findByBookId(UUID bookUuid);
    List<Copy> findByBookIdAndAvailableTrue(UUID bookId);    Page<Copy> findByAvailableTrueAndDeletedFalse(Pageable pageable);
    long countByBook_Id(UUID bookId);}
