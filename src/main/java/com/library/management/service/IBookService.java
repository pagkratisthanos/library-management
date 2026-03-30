package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.BookInsertDTO;
import com.library.management.dto.BookReadOnlyDTO;
import com.library.management.dto.BookUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IBookService {

    BookReadOnlyDTO saveBook(BookInsertDTO dto) throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException ;

    BookReadOnlyDTO updateBook(BookUpdateDTO dto) throws EntityNotFoundException, EntityInvalidArgumentException;

    void deleteBookByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    BookReadOnlyDTO getBookByUuid(UUID uuid) throws EntityNotFoundException;

    BookReadOnlyDTO getBookByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<BookReadOnlyDTO> getBooksPaginated(Pageable pageable);

    Page<BookReadOnlyDTO> getBooksPaginatedAndDeletedFalse(Pageable pageable);

    boolean isBookExistByIsbn(String isbn);
}
