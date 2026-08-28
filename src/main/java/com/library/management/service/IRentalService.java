package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.RentalFilters;
import com.library.management.dto.RentalExtendDTO;
import com.library.management.dto.RentalInsertDTO;
import com.library.management.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Lending operations.
 *
 * <p>A rental is <em>active</em> while its return date is null, and <em>overdue</em> while it is
 * active and its due date has passed. No rental may last more than 90 days from the day it started.
 *
 * <p>Lending and returning also flip the availability flag on the copy, so the two writes happen
 * inside one transaction: a copy can never be marked as lent without a rental to account for it.
 */
public interface IRentalService {

    /**
     * Lends a copy to a member and marks that copy unavailable.
     *
     * @throws EntityNotFoundException        if the member or the copy does not exist, or has been
     *                                        soft-deleted
     * @throws EntityInvalidArgumentException if the copy is already lent out, or the requested due
     *                                        date is in the past or more than 90 days away
     */
    Rental saveRental(RentalInsertDTO dto) throws EntityNotFoundException, EntityInvalidArgumentException;

    /**
     * Closes the rental and puts the copy back into circulation.
     *
     * @throws EntityNotFoundException        if no rental has this id
     * @throws EntityInvalidArgumentException if the rental has already been returned
     */
    Rental returnRental(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    Rental getRentalByUuid(UUID uuid) throws EntityNotFoundException;

    /**
     * The member's whole history, returned rentals included.
     *
     * @throws EntityNotFoundException if no member has this id
     */
    List<Rental> getRentalsByMemberUuid(UUID memberUuid) throws EntityNotFoundException;

    /**
     * The copy's whole history, returned rentals included.
     *
     * @throws EntityNotFoundException if no copy has this id
     */
    List<Rental> getRentalsByCopyUuid(UUID copyUuid) throws EntityNotFoundException;

    Page<Rental> getRentalsPaginated(Pageable pageable);

    /** Rentals that have not been returned, whether or not they are late. */
    Page<Rental> getActiveRentalsPaginated(Pageable pageable);

    Page<Rental> getRentalsPaginatedFiltered(Pageable pageable, RentalFilters filters);

    /**
     * Moves the due date further out. The copy stays with the member.
     *
     * @throws EntityNotFoundException        if no rental has this id
     * @throws EntityInvalidArgumentException if the rental has already been returned, if the new
     *                                        due date is not later than the current one, or if it
     *                                        would push the rental past 90 days in total
     */
    Rental extendRental(UUID uuid, RentalExtendDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException;

    /** Active rentals whose due date has passed, evaluated at the moment of the call. */
    Page<Rental> getOverdueRentalsPaginated(Pageable pageable);
}
