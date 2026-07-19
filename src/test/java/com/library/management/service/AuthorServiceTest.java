package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.AuthorInsertDTO;
import com.library.management.dto.AuthorUpdateDTO;
import com.library.management.model.Author;
import com.library.management.model.Book;
import com.library.management.repository.AuthorRepository;
import com.library.management.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class AuthorServiceTest {

    @Autowired
    private IAuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private Author existingAuthor;

    @BeforeEach
    void setUp() {
        existingAuthor = new Author();
        existingAuthor.setFirstname("George");
        existingAuthor.setLastname("Orwell");
        existingAuthor.setBirthDate(LocalDate.of(1903, 6, 25));
        authorRepository.save(existingAuthor);
    }

    @Test
    void saveAuthor_whenValidData_shouldSaveAndReturnAuthor() throws EntityInvalidArgumentException {
        AuthorInsertDTO dto = new AuthorInsertDTO(
                "John", "Tolkien", LocalDate.of(1892, 1, 3), "England", "Author of LOTR"
        );

        Author saved = authorService.saveAuthor(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstname()).isEqualTo("John");
        assertThat(saved.getLastname()).isEqualTo("Tolkien");
    }

    @Test
    void saveAuthor_whenBirthDateInFuture_shouldThrowException() {
        AuthorInsertDTO dto = new AuthorInsertDTO(
                "John", "Tolkien", LocalDate.now().plusDays(1), "England", "Author of LOTR"
        );

        assertThatThrownBy(() -> authorService.saveAuthor(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void updateAuthor_whenAuthorExists_shouldUpdateAndReturn() throws EntityNotFoundException {
        AuthorUpdateDTO dto = new AuthorUpdateDTO(
                "Updated", "Orwell", LocalDate.of(1903, 6, 25), "India", "Updated bio"
        );

        Author updated = authorService.updateAuthor(existingAuthor.getId(), dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getFirstname()).isEqualTo("Updated");
        assertThat(updated.getBio()).isEqualTo("Updated bio");
    }

    @Test
    void updateAuthor_whenAuthorNotFound_shouldThrowException() {
        AuthorUpdateDTO dto = new AuthorUpdateDTO(
                "Updated", "Orwell", LocalDate.of(1903, 6, 25), "India", "Updated bio"
        );

        assertThatThrownBy(() -> authorService.updateAuthor(UUID.randomUUID(), dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteAuthorByUuid_whenAuthorHasNoBooksWithSingleAuthor_shouldSoftDelete()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        authorService.deleteAuthorByUuid(existingAuthor.getId());

        Author deleted = authorRepository.findById(existingAuthor.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
        assertThat(deleted.getDeletedAt()).isNotNull();
    }

    @Test
    void deleteAuthorByUuid_whenAuthorNotFound_shouldThrowException() {
        assertThatThrownBy(() -> authorService.deleteAuthorByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteAuthorByUuid_whenAuthorHasBookWithSingleAuthor_shouldThrowException() {
        Book book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);

        existingAuthor.addBook(book);
        book.addAuthor(existingAuthor);
        authorRepository.save(existingAuthor);
        bookRepository.save(book);

        assertThatThrownBy(() -> authorService.deleteAuthorByUuid(existingAuthor.getId()))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void getAuthorByUUIDDeletedFalse_whenExists_shouldReturnAuthor() throws EntityNotFoundException {
        Author found = authorService.getAuthorByUUIDDeletedFalse(existingAuthor.getId());
        assertThat(found).isNotNull();
        assertThat(found.getFirstname()).isEqualTo("George");
    }

    @Test
    void getAuthorByUUIDDeletedFalse_whenDeleted_shouldThrowException() {
        existingAuthor.softDelete();
        authorRepository.save(existingAuthor);

        assertThatThrownBy(() -> authorService.getAuthorByUUIDDeletedFalse(existingAuthor.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAuthorByUUIDDeletedFalse_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> authorService.getAuthorByUUIDDeletedFalse(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getAuthorsPaginatedAndDeletedFalse_shouldReturnOnlyActiveAuthors() {
        existingAuthor.softDelete();
        authorRepository.save(existingAuthor);

        Author activeAuthor = new Author();
        activeAuthor.setFirstname("Active");
        activeAuthor.setLastname("Author");
        authorRepository.save(activeAuthor);

        Page<Author> authors = authorService.getAuthorsPaginatedAndDeletedFalse(PageRequest.of(0, 10));
        assertThat(authors.getContent()).hasSize(1);
        assertThat(authors.getContent().get(0).getFirstname()).isEqualTo("Active");
    }

    @Test
    void isAuthorExistByLastname_whenExists_shouldReturnTrue() {
        boolean exists = authorService.isAuthorExistByLastname("Orwell");
        assertThat(exists).isTrue();
    }

    @Test
    void isAuthorExistByLastname_whenNotExists_shouldReturnFalse() {
        boolean exists = authorService.isAuthorExistByLastname("Tolkien");
        assertThat(exists).isFalse();
    }

    @Test
    void getAuthorsByBookUuid_whenBookExists_shouldReturnAuthors() throws EntityNotFoundException {
        Book book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);

        existingAuthor.addBook(book);
        book.addAuthor(existingAuthor);
        authorRepository.save(existingAuthor);
        bookRepository.save(book);

        List<Author> authors = authorService.getAuthorsByBookUuid(book.getId());
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getFirstname()).isEqualTo("George");
    }

    @Test
    void getAuthorsByBookUuid_whenBookNotFound_shouldThrowException() {
        assertThatThrownBy(() -> authorService.getAuthorsByBookUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}