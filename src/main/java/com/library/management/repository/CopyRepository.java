package com.library.management.repository;

import com.library.management.model.Copy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CopyRepository extends JpaRepository<Copy, Long> {

    Optional<Copy> findByUuid(UUID uuid);
    Optional<Copy> findByUuidAndDeletedFalse(UUID uuid);
    Page<Copy> findByDeletedFalse(Pageable pageable);
    boolean existsByUuid(UUID uuid);
    List<Copy> findByBook_Uuid(UUID bookUuid);
    List<Copy> findByBook_UuidAndAvailableTrue(UUID bookUuid);
    Page<Copy> findByAvailableTrueAndDeletedFalse(Pageable pageable);
    long countByBook_Uuid(UUID bookUuid);
}
