package com.library.management.specification;

import com.library.management.core.filters.AuthorFilters;
import com.library.management.model.Author;
import com.library.management.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AuthorSpecificationTest {

    @Autowired
    private AuthorRepository authorRepository;

    private Author orwell;
    private Author tolkien;

    @BeforeEach
    void setUp() {
        orwell = new Author();
        orwell.setFirstname("George");
        orwell.setLastname("Orwell");
        orwell.setBirthDate(LocalDate.of(1903, 6, 25));
        orwell.setBirthPlace("India");
        authorRepository.save(orwell);

        tolkien = new Author();
        tolkien.setFirstname("John");
        tolkien.setLastname("Tolkien");
        tolkien.setBirthDate(LocalDate.of(1892, 1, 3));
        tolkien.setBirthPlace("England");
        authorRepository.save(tolkien);
    }

    @Test
    void build_whenNoFilters_shouldReturnAllActiveAuthors() {
        AuthorFilters filters = new AuthorFilters();
        Page<Author> result = authorRepository.findAll(AuthorSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void build_whenFilterByLastname_shouldReturnMatchingAuthors() {
        AuthorFilters filters = new AuthorFilters();
        filters.setLastname("Orwell");
        Page<Author> result = authorRepository.findAll(AuthorSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastname()).isEqualTo("Orwell");
    }

    @Test
    void build_whenFilterByFirstname_shouldReturnMatchingAuthors() {
        AuthorFilters filters = new AuthorFilters();
        filters.setFirstname("John");
        Page<Author> result = authorRepository.findAll(AuthorSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstname()).isEqualTo("John");
    }

    @Test
    void build_whenFilterByBirthPlace_shouldReturnMatchingAuthors() {
        AuthorFilters filters = new AuthorFilters();
        filters.setBirthPlace("India");
        Page<Author> result = authorRepository.findAll(AuthorSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBirthPlace()).isEqualTo("India");
    }

    @Test
    void build_whenFilterByPartialLastname_shouldReturnMatchingAuthors() {
        AuthorFilters filters = new AuthorFilters();
        filters.setLastname("ell");
        Page<Author> result = authorRepository.findAll(AuthorSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastname()).isEqualTo("Orwell");
    }

    @Test
    void build_whenDeletedAuthor_shouldNotReturn() {
        orwell.softDelete();
        authorRepository.save(orwell);

        AuthorFilters filters = new AuthorFilters();
        Page<Author> result = authorRepository.findAll(AuthorSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastname()).isEqualTo("Tolkien");
    }
}