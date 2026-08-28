package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.UserFilters;
import com.library.management.dto.PasswordChangeDTO;
import com.library.management.dto.UserInsertDTO;
import com.library.management.dto.UserPasswordUpdateDTO;
import com.library.management.dto.UserRoleUpdateDTO;
import com.library.management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Accounts, roles and passwords.
 *
 * <p>Passwords are hashed with BCrypt on the way in and are never returned in readable form.
 * Two different operations change a password: an administrator resetting someone else's
 * ({@link #updateUserPassword}), and a user changing their own ({@link #changeOwnPassword}).
 * Only the second one asks for the current password.
 */
public interface IUserService {

    /**
     * @throws EntityAlreadyExistsException   if the username is taken, including by a soft-deleted
     *                                        account
     * @throws EntityInvalidArgumentException if no role has the requested id
     */
    User saveUser(UserInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    /** Finds the account whether or not it has been soft-deleted. */
    User getUserByUuid(UUID uuid) throws EntityNotFoundException;

    /** Finds the account only while it is still active. */
    User getUserByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    boolean isUserExists(String username);

    void deleteUserByUuid(UUID uuid) throws EntityNotFoundException;

    Page<User> getAllUsers(UserFilters filters, Pageable pageable);

    /**
     * Changes the caller's own password after verifying the one they currently hold.
     *
     * @param username taken from the authenticated principal, not from the request body, so that
     *                 nobody can change an account other than their own
     * @throws EntityNotFoundException        if the account is missing or soft-deleted
     * @throws EntityInvalidArgumentException if the current password is wrong, or the new password
     *                                        is the same as the current one
     */
    User changeOwnPassword(String username, PasswordChangeDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException;

    /**
     * Moves the account to another role. Asking for the role it already has is a no-op.
     *
     * @throws EntityNotFoundException        if the account is missing or soft-deleted
     * @throws EntityInvalidArgumentException if no role has this id, or if the change would remove
     *                                        the last remaining administrator and lock everyone out
     */
    User updateUserRole(UUID uuid, UserRoleUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException;

    /**
     * Sets a new password without asking for the old one. This is the administrator's reset, for
     * the case where the user cannot sign in at all.
     *
     * @throws EntityNotFoundException if the account is missing or soft-deleted
     */
    User updateUserPassword(UUID uuid, UserPasswordUpdateDTO dto)
            throws EntityNotFoundException;
}
