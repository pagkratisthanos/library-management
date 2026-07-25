package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.UserInsertDTO;
import com.library.management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {

    User saveUser(UserInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    User getUserByUuid(UUID uuid) throws EntityNotFoundException;

    User getUserByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    boolean isUserExists(String username);

    void deleteUserByUuid(UUID uuid) throws EntityNotFoundException;

    Page<User> getAllUsers(Pageable pageable);
}