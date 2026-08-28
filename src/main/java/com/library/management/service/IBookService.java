package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.BookFilters;
import com.library.management.dto.BookInsertDTO;
import com.library.management.dto.BookUpdateDTO;
import com.library.management.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * The catalogue.
 *
 * <p>A book is a title, not a physical object. What gets lent is a {@link com.library.management.model.Copy},
 * which is why a book can exist with no copies at all.
 */
public interface IBookService {

    /**
     * @throws EntityAlreadyExistsException   if any book already uses this ISBN, including one that
     *                                        has been soft-deleted — the unique constraint on the
     *                                        column counts those too
     * @throws EntityInvalidArgumentException if the daily cost is negative or the publication date
     *                                        is in the future
     * @throws EntityNotFoundException        if one of the listed authors does not exist
     */
    Book saveBook(BookInsertDTO dto) throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException ;

    /**
     * Updates the title's own fields. The set of authors is managed separately and is not touched
     * here.
     *
     * @throws EntityNotFoundException        if no book has this id
     * @throws EntityInvalidArgumentException if the daily cost is negative
     * @throws EntityAlreadyExistsException   if the new ISBN belongs to another book
     */
    Book updateBook(UUID id, BookUpdateDTO dto) throws EntityNotFoundException, EntityInvalidArgumentException, EntityAlreadyExistsException;

    /**
     * Soft-deletes the book together with all of its copies, so the catalogue and the shelf stay
     * consistent.
     *
     * @throws EntityNotFoundException        if no book has this id
     * @throws EntityInvalidArgumentException if any copy of the book is currently lent out
     */
    void deleteBookByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    /** Finds the book whether or not it has been soft-deleted. */
    Book getBookByUuid(UUID uuid) throws EntityNotFoundException;

    /** Finds the book only while it is still in the catalogue. */
    Book getBookByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<Book> getBooksPaginated(Pageable pageable);

    Page<Book> getBooksPaginatedAndDeletedFalse(Pageable pageable);

    boolean isBookExistByIsbn(String isbn);

    Page<Book> getBooksPaginatedFilteredAndDeletedFalse(Pageable pageable, BookFilters filters);
}
