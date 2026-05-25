package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.RentalInsertDTO;
import com.library.management.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IRentalService {

    Rental saveRental(RentalInsertDTO dto) throws EntityNotFoundException, EntityInvalidArgumentException;

    Rental returnRental(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    Rental getRentalByUuid(UUID uuid) throws EntityNotFoundException;

    List<Rental> getRentalsByMemberUuid(UUID memberUuid) throws EntityNotFoundException;

    List<Rental> getRentalsByCopyUuid(UUID copyUuid) throws EntityNotFoundException;

    Page<Rental> getRentalsPaginated(Pageable pageable);

    Page<Rental> getActiveRentalsPaginated(Pageable pageable);
}
