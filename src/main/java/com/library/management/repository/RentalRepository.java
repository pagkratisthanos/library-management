package com.library.management.repository;

import com.library.management.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, UUID>, JpaSpecificationExecutor<Rental> {

    /** The mapper reads the member, the copy and the copy's book. */
    @Override
    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    Page<Rental> findAll(Specification<Rental> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    Optional<Rental> findById(UUID uuid);

    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    List<Rental> findByMember_Id(UUID memberUuid);

    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    List<Rental> findByCopy_Id(UUID copyUuid);

    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    Page<Rental> findByReturnDateIsNull(Pageable pageable);

    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    Page<Rental> findByReturnDateIsNullAndDueDateBefore(Instant moment, Pageable pageable);

    @Override
    boolean existsById(UUID uuid);
}