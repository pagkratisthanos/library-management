package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.MemberFilters;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberUpdateDTO;
import com.library.management.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * The people who borrow books.
 *
 * <p>Email and phone number are unique among active members. A member owns exactly one address,
 * which lives and dies with them: it is created on insert, edited in place on update, and
 * soft-deleted together with the member.
 */
public interface IMemberService {

    /**
     * @throws EntityAlreadyExistsException   if the email or phone number belongs to another active
     *                                        member
     * @throws EntityInvalidArgumentException if the birth date or the membership date is in the
     *                                        future
     */
    Member saveMember(MemberInsertDTO dto) throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    /**
     * Updates the member and their existing address. The address row is mutated rather than
     * replaced, so its id survives the update.
     *
     * @throws EntityNotFoundException        if no member has this id
     * @throws EntityAlreadyExistsException   if the email or phone number belongs to another member
     * @throws EntityInvalidArgumentException if the birth date or the membership date is in the
     *                                        future
     */
    Member updateMember(UUID id, MemberUpdateDTO dto) throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidArgumentException;

    /**
     * Soft-deletes the member and their address.
     *
     * @throws EntityNotFoundException        if the member is missing or already deleted
     * @throws EntityInvalidArgumentException if the member still has a book out
     */
    void deleteMemberByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    /** Finds the member whether or not they have been soft-deleted. */
    Member getMemberByUuid(UUID uuid) throws EntityNotFoundException;

    /** Finds the member only while the membership is still active. */
    Member getMemberByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<Member> getMembersPaginated(Pageable pageable);

    Page<Member> getMembersPaginatedAndDeletedFalse(Pageable pageable);

    boolean isMemberExistByEmail(String email);

    Member getMemberByEmail(String email) throws EntityNotFoundException;

    Member getMemberByPhoneNumber(String phoneNumber) throws EntityNotFoundException;

    Page<Member> getMembersPaginatedFilteredAndDeletedFalse(Pageable pageable, MemberFilters filters);

}
