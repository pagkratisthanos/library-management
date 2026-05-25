package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.AuthorInsertDTO;
import com.library.management.dto.AuthorUpdateDTO;
import com.library.management.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAuthorService {

    Author saveAuthor(AuthorInsertDTO dto) throws EntityInvalidArgumentException;

    Author updateAuthor(UUID id, AuthorUpdateDTO dto) throws EntityNotFoundException;

    Author getAuthorByUuid(UUID uuid) throws EntityNotFoundException;

    Author getAuthorByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<Author> getAuthorsPaginated(Pageable pageable);

    Page<Author> getAuthorsPaginatedAndDeletedFalse(Pageable pageable);

    List<Author> getAuthorsByBookUuid(UUID bookUuid) throws EntityNotFoundException;

    boolean isAuthorExistByLastname(String lastname);

    void deleteAuthorByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

}
