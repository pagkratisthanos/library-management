package com.library.management.repository;

import com.library.management.model.Copy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CopyRepository extends JpaRepository<Copy, UUID>, JpaSpecificationExecutor<Copy> {

    /** The mapper reads copy.getBook(), so the book comes along in the same query. */
    @Override
    @EntityGraph(attributePaths = {"book"})
    Page<Copy> findAll(Specification<Copy> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"book"})
    Optional<Copy> findById(UUID uuid);

    @EntityGraph(attributePaths = {"book"})
    Optional<Copy> findByIdAndDeletedFalse(UUID uuid);

    @EntityGraph(attributePaths = {"book"})
    Page<Copy> findByDeletedFalse(Pageable pageable);

    @Override
    boolean existsById(UUID uuid);
    List<Copy> findByBookId(UUID bookUuid);
    List<Copy> findByBookIdAndAvailableTrue(UUID bookId);
    Page<Copy> findByAvailableTrueAndDeletedFalse(Pageable pageable);
    long countByBook_Id(UUID bookId);
}