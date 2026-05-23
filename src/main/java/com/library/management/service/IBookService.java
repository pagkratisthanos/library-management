package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.BookInsertDTO;
import com.library.management.dto.BookUpdateDTO;
import com.library.management.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IBookService {

    Book saveBook(BookInsertDTO dto) throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException ;

    Book updateBook(UUID id, BookUpdateDTO dto) throws EntityNotFoundException, EntityInvalidArgumentException;

    void deleteBookByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    Book getBookByUuid(UUID uuid) throws EntityNotFoundException;

    Book getBookByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<Book> getBooksPaginated(Pageable pageable);

    Page<Book> getBooksPaginatedAndDeletedFalse(Pageable pageable);

    boolean isBookExistByIsbn(String isbn);
}
