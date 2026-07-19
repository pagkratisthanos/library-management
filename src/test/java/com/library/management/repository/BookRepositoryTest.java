package com.library.management.repository;

import com.library.management.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);
    }

    @Test
    void findByIdAndDeletedFalse_whenBookExists_shouldReturnBook() {
        Optional<Book> found = bookRepository.findByIdAndDeletedFalse(book.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Animal Farm");
    }

    @Test
    void findByIdAndDeletedFalse_whenBookDeleted_shouldReturnEmpty() {
        book.softDelete();
        bookRepository.save(book);

        Optional<Book> found = bookRepository.findByIdAndDeletedFalse(book.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findByIsbn_whenExists_shouldReturnBook() {
        Optional<Book> found = bookRepository.findByIsbn("978-0-452-28424-4");
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Animal Farm");
    }

    @Test
    void findByIsbn_whenNotExists_shouldReturnEmpty() {
        Optional<Book> found = bookRepository.findByIsbn("000-0-000-00000-0");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByIsbn_whenExists_shouldReturnTrue() {
        boolean exists = bookRepository.existsByIsbn("978-0-452-28424-4");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByIsbn_whenNotExists_shouldReturnFalse() {
        boolean exists = bookRepository.existsByIsbn("000-0-000-00000-0");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByIsbnAndDeletedFalse_whenExistsAndNotDeleted_shouldReturnTrue() {
        boolean exists = bookRepository.existsByIsbnAndDeletedFalse("978-0-452-28424-4");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByIsbnAndDeletedFalse_whenDeleted_shouldReturnFalse() {
        book.softDelete();
        bookRepository.save(book);

        boolean exists = bookRepository.existsByIsbnAndDeletedFalse("978-0-452-28424-4");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByIsbnAndIdNot_whenSameIsbnDifferentId_shouldReturnTrue() {
        Book anotherBook = new Book();
        anotherBook.setTitle("1984");
        anotherBook.setIsbn("978-0-452-28424-5"); // ← διαφορετικό ISBN
        anotherBook.setLanguage("English");
        anotherBook.setDailyCost(BigDecimal.valueOf(1.50));
        bookRepository.save(anotherBook);

        boolean exists = bookRepository.existsByIsbnAndIdNot("978-0-452-28424-5", book.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByIsbnAndIdNot_whenSameBookId_shouldReturnFalse() {
        boolean exists = bookRepository.existsByIsbnAndIdNot("978-0-452-28424-4", book.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void findByDeletedFalse_shouldReturnOnlyActiveBooks() {
        Book deletedBook = new Book();
        deletedBook.setTitle("Deleted Book");
        deletedBook.setIsbn("000-0-000-00000-0");
        deletedBook.setLanguage("English");
    }
}