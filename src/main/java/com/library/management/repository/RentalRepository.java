package com.library.management.repository;

import com.library.management.model.Rental;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, Long> {

        Optional<Rental> findByUuid(UUID uuid);
        List<Rental> findByMember_Uuid(UUID memberUuid);
        List<Rental> findByCopy_Uuid(UUID copyUuid);
        Page<Rental> findByReturnDateIsNull(Pageable pageable);
        boolean existsByUuid(UUID uuid);
}
