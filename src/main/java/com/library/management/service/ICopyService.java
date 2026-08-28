package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.CopyFilters;
import com.library.management.dto.CopyInsertDTO;
import com.library.management.dto.CopyUpdateDTO;
import com.library.management.model.Copy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * The physical items on the shelf. Several copies of the same book are distinguishable only by
 * their id and their condition.
 *
 * <p>The {@code available} flag is normally driven by
 * {@link IRentalService#saveRental} and {@link IRentalService#returnRental}. Setting it by hand
 * through this service is meant for stock corrections, and is refused where it would contradict an
 * open rental.
 */
public interface ICopyService {

    /**
     * @throws EntityNotFoundException        if no book has this id
     * @throws EntityInvalidArgumentException if the book has been soft-deleted
     */
    Copy saveCopy(CopyInsertDTO dto) throws EntityInvalidArgumentException, EntityNotFoundException;

    /**
     * @throws EntityNotFoundException        if no copy has this id
     * @throws EntityInvalidArgumentException if the copy is being marked available while a rental
     *                                        for it is still open
     */
    Copy updateCopy(UUID id, CopyUpdateDTO dto) throws EntityNotFoundException, EntityInvalidArgumentException;

    /**
     * @throws EntityNotFoundException        if no copy has this id
     * @throws EntityInvalidArgumentException if the copy is currently lent out
     */
    void deleteCopyByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    /** Finds the copy whether or not it has been soft-deleted. */
    Copy getCopyByUuid(UUID uuid) throws EntityNotFoundException;

    /** Finds the copy only while it is still on the shelf. */
    Copy getCopyByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<Copy> getCopiesPaginated(Pageable pageable);

    Page<Copy> getCopiesPaginatedAndDeletedFalse(Pageable pageable);

    Page<Copy> getCopiesPaginatedFilteredAndDeletedFalse(Pageable pageable, CopyFilters filters);
}
