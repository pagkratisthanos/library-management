package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.RentalInsertDTO;
import com.library.management.dto.RentalReadOnlyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IRentalService {

    RentalReadOnlyDTO saveRental(RentalInsertDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException;

    RentalReadOnlyDTO returnRental(UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException;

    RentalReadOnlyDTO getRentalByUuid(UUID uuid) throws EntityNotFoundException;

    List<RentalReadOnlyDTO> getRentalsByMemberUuid(UUID memberUuid) throws EntityNotFoundException;

    List<RentalReadOnlyDTO> getRentalsByCopyUuid(UUID copyUuid) throws EntityNotFoundException;

    Page<RentalReadOnlyDTO> getRentalsPaginated(Pageable pageable);

    Page<RentalReadOnlyDTO> getActiveRentalsPaginated(Pageable pageable);
}
