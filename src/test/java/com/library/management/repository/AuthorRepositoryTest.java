package com.library.management.repository;

import com.library.management.model.Author;
import com.library.management.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setFirstname("George");
        author.setLastname("Orwell");
        author.setBirthDate(LocalDate.of(1903, 6, 25));
        authorRepository.save(author);
    }

    @Test
    void findByIdAndDeletedFalse_whenAuthorExists_shouldReturnAuthor() {
        Optional<Author> found = authorRepository.findByIdAndDeletedFalse(author.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstname()).isEqualTo("George");
    }

    @Test
    void findByIdAndDeletedFalse_whenAuthorDeleted_shouldReturnEmpty() {
        author.softDelete();
        authorRepository.save(author);

        Optional<Author> found = authorRepository.findByIdAndDeletedFalse(author.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findByDeletedFalse_shouldReturnOnlyActiveAuthors() {
        Author deletedAuthor = new Author();
        deletedAuthor.setFirstname("Deleted");
        deletedAuthor.setLastname("Author");
        deletedAuthor.softDelete();
        authorRepository.save(deletedAuthor);

        Page<Author> authors = authorRepository.findByDeletedFalse(PageRequest.of(0, 10));
        assertThat(authors.getContent()).hasSize(1);
        assertThat(authors.getContent().get(0).getFirstname()).isEqualTo("George");
    }

    @Test
    void existsByLastname_whenExists_shouldReturnTrue() {
        boolean exists = authorRepository.existsByLastname("Orwell");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByLastname_whenNotExists_shouldReturnFalse() {
        boolean exists = authorRepository.existsByLastname("Tolkien");
        assertThat(exists).isFalse();
    }

    @Test
    void findByBooks_Id_shouldReturnAuthorsOfBook() {
        Book book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);

        author.addBook(book);
        authorRepository.save(author);

        List<Author> authors = authorRepository.findByBooks_Id(book.getId());
        assertThat(authors).hasSize(1);
        assertThat(authors.get(0).getFirstname()).isEqualTo("George");
    }

    @Test
    void findByBooks_Id_whenNoAuthors_shouldReturnEmptyList() {
        Book book = new Book();
        book.setTitle("1984");
        book.setIsbn("978-0-452-28423-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.50));
        bookRepository.save(book);

        List<Author> authors = authorRepository.findByBooks_Id(book.getId());
        assertThat(authors).isEmpty();
    }
}