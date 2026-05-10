package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.AuthorInsertDTO;
import com.library.management.dto.AuthorReadOnlyDTO;
import com.library.management.dto.AuthorUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAuthorService {

    AuthorReadOnlyDTO saveAuthor(AuthorInsertDTO authorInsertDTO)
            throws EntityInvalidArgumentException;

    AuthorReadOnlyDTO updateAuthor(UUID id, AuthorUpdateDTO authorUpdateDTO)
            throws EntityNotFoundException;

    void deleteAuthorByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    AuthorReadOnlyDTO getAuthorByUuid(UUID uuid) throws EntityNotFoundException;

    AuthorReadOnlyDTO getAuthorByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<AuthorReadOnlyDTO> getAuthorsPaginated(Pageable pageable);

    Page<AuthorReadOnlyDTO> getAuthorsPaginatedAndDeletedFalse(Pageable pageable);

    boolean isAuthorExistByLastname(String lastname);

    List<AuthorReadOnlyDTO> getAuthorsByBookUuid(UUID bookUuid) throws EntityNotFoundException;

}
