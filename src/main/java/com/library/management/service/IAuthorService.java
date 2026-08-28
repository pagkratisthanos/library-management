package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.AuthorFilters;
import com.library.management.dto.AuthorInsertDTO;
import com.library.management.dto.AuthorUpdateDTO;
import com.library.management.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Authors, and their many-to-many link to books.
 *
 * <p>Authors are not unique by name: two people may share one, and the birth date is optional
 * because it is genuinely unknown for some writers.
 */
public interface IAuthorService {

    /**
     * @throws EntityInvalidArgumentException if the birth date is in the future
     */
    Author saveAuthor(AuthorInsertDTO dto) throws EntityInvalidArgumentException;

    Author updateAuthor(UUID id, AuthorUpdateDTO dto) throws EntityNotFoundException;

    /** Finds the author whether or not they have been soft-deleted. */
    Author getAuthorByUuid(UUID uuid) throws EntityNotFoundException;

    /** Finds the author only while the record is still active. */
    Author getAuthorByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<Author> getAuthorsPaginated(Pageable pageable);

    Page<Author> getAuthorsPaginatedAndDeletedFalse(Pageable pageable);

    /**
     * @throws EntityNotFoundException if no book has this id
     */
    List<Author> getAuthorsByBookUuid(UUID bookUuid) throws EntityNotFoundException;

    boolean isAuthorExistByLastname(String lastname);

    /**
     * Soft-deletes the author, but only if no book would be left with nobody credited. An author of
     * a co-written book can therefore be removed while the sole author of a book cannot; deleted
     * authors and deleted books are ignored when counting.
     *
     * @throws EntityNotFoundException        if no author has this id
     * @throws EntityInvalidArgumentException if at least one of their books has no other author
     */
    void deleteAuthorByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    Page<Author> getAuthorsPaginatedFilteredAndDeletedFalse(Pageable pageable, AuthorFilters filters);

}
