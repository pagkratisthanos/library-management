package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.BookInsertDTO;
import com.library.management.dto.BookReadOnlyDTO;
import com.library.management.dto.BookUpdateDTO;
import com.library.management.mapper.BookMapper;
import com.library.management.model.Author;
import com.library.management.model.Book;
import com.library.management.model.Rental;
import com.library.management.repository.AuthorRepository;
import com.library.management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements IBookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper mapper;

    @Override
    @Transactional(rollbackFor = {EntityAlreadyExistsException.class,
            EntityInvalidArgumentException.class,
            EntityNotFoundException.class})
    public BookReadOnlyDTO saveBook(BookInsertDTO dto) throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {

        try {
            if (bookRepository.existsByIsbn(dto.isbn())) {
                throw new EntityAlreadyExistsException("Book", "Book with isbn: " + dto.isbn() + " already exists.");
            }

            if (dto.dailyCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new EntityInvalidArgumentException("Book", "Daily cost cannot be negative");
            }

            if (dto.publishedDate() != null && dto.publishedDate().isAfter(LocalDate.now())) {
                throw new EntityInvalidArgumentException("Book", "Published date cannot be in the future");
            }

            Set<Author> authors = new HashSet<>();
            if (dto.authorUuids() != null) {
                for (UUID uuid : dto.authorUuids()) {
                    Author author = authorRepository.findByUuid(uuid)
                            .orElseThrow(() -> new EntityNotFoundException("Author", "Author with uuid=" + uuid + " not found"));
                    authors.add(author);
                }
            }

            Book book = mapper.mapToBookEntity(dto);
            Book savedBook = bookRepository.save(book);

            for (Author author : authors) {
                author.addBook(savedBook);
                authorRepository.save(author);
            }

            for (Author author : authors) {
                savedBook.addAuthor(author);
            }

            log.info("Book with isbn={} has been saved successfully", savedBook.getIsbn());
            return mapper.mapToBookReadOnlyDTO(savedBook);

        } catch (EntityNotFoundException | EntityAlreadyExistsException | EntityInvalidArgumentException e) {
            log.error("Save book failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class,
            EntityInvalidArgumentException.class})
    public BookReadOnlyDTO updateBook(BookUpdateDTO dto) throws EntityNotFoundException,
            EntityInvalidArgumentException {
        try {
            Book book = bookRepository.findByUuid(dto.uuid())
                    .orElseThrow(() -> new EntityNotFoundException("Book", "Book with uuid=" + dto.uuid() + " not found"));

            if (dto.dailyCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new EntityInvalidArgumentException("Book", "Daily cost cannot be negative");
            }

            book.setLanguage(dto.language());
            book.setDailyCost(dto.dailyCost());
            book.setDescription(dto.description());

            Book updatedBook = bookRepository.save(book);
            log.info("Book updated with uuid={}", updatedBook.getId());

            return mapper.mapToBookReadOnlyDTO(updatedBook);
        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.error("Update failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public void deleteBookByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException {
        try {
            Book book = bookRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Book", "Book with uuid " + uuid + " not found"));

            boolean hasActiveRentals = book.getAllCopies().stream()
                    .anyMatch(copy -> copy.getAllRentals().stream()
                            .anyMatch(Rental::isActive));

            if (hasActiveRentals) {
                throw new EntityInvalidArgumentException("Book", "Cannot delete book with active rentals");
            }

            book.softDelete();
            book.getAllCopies().forEach(copy -> copy.softDelete());
            bookRepository.save(book);
            log.info("Book with uuid={} has successfully been deleted", uuid);

        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.error("Book delete failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookReadOnlyDTO getBookByUuid(UUID uuid) throws EntityNotFoundException {
        try {
            Book book = bookRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Book", "Book with uuid " + uuid + " not found"));

            log.info("Get book by uuid={} returned successfully.", uuid);
            return mapper.mapToBookReadOnlyDTO(book);
        } catch (EntityNotFoundException e) {
            log.error("Get book by uuid={} failed. {}", uuid, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookReadOnlyDTO getBookByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException {
        try {
            Book book = bookRepository.findByUuidAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Book", "Book with uuid: " + uuid + " not found"));

            log.info("Get non-deleted book by uuid={} returned successfully.", uuid);
            return mapper.mapToBookReadOnlyDTO(book);
        } catch (EntityNotFoundException e) {
            log.error("Get book by uuid={} failed. {}", uuid, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookReadOnlyDTO> getBooksPaginated(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        log.info("Get paginated returned successfully page={} and size={}", bookPage.getNumber(), bookPage.getSize());
        return bookPage.map(mapper::mapToBookReadOnlyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookReadOnlyDTO> getBooksPaginatedAndDeletedFalse(Pageable pageable) {
        Page<Book> memberPage = bookRepository.findByDeletedFalse(pageable);
        log.info("Get paginated not deleted returned successfully page={} and size={}", memberPage.getNumber(), memberPage.getSize());
        return memberPage.map(mapper::mapToBookReadOnlyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookExistByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }
}