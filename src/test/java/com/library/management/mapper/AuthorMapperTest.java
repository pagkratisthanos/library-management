package com.library.management.mapper;

import com.library.management.dto.AuthorInsertDTO;
import com.library.management.dto.AuthorReadOnlyDTO;
import com.library.management.model.Author;
import com.library.management.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorMapperTest {

    private AuthorMapper authorMapper;
    private Author author;

    @BeforeEach
    void setUp() {
        authorMapper = new AuthorMapper();

        author = new Author();
        author.setFirstname("George");
        author.setLastname("Orwell");
        author.setBirthDate(LocalDate.of(1903, 6, 25));
        author.setBirthPlace("Motihari, India");
        author.setBio("English novelist");
    }

    @Test
    void mapToAuthorEntity_shouldMapCorrectly() {
        AuthorInsertDTO dto = new AuthorInsertDTO(
                "George", "Orwell",
                LocalDate.of(1903, 6, 25),
                "Motihari, India", "English novelist"
        );

        Author mapped = authorMapper.mapToAuthorEntity(dto);

        assertThat(mapped.getFirstname()).isEqualTo("George");
        assertThat(mapped.getLastname()).isEqualTo("Orwell");
        assertThat(mapped.getBirthDate()).isEqualTo(LocalDate.of(1903, 6, 25));
        assertThat(mapped.getBirthPlace()).isEqualTo("Motihari, India");
        assertThat(mapped.getBio()).isEqualTo("English novelist");
    }

    @Test
    void mapToAuthorReadOnlyDTO_shouldMapCorrectly() {
        AuthorReadOnlyDTO dto = authorMapper.mapToAuthorReadOnlyDTO(author);

        assertThat(dto.firstname()).isEqualTo("George");
        assertThat(dto.lastname()).isEqualTo("Orwell");
        assertThat(dto.birthDate()).isEqualTo(LocalDate.of(1903, 6, 25));
        assertThat(dto.birthPlace()).isEqualTo("Motihari, India");
        assertThat(dto.bio()).isEqualTo("English novelist");
    }

    @Test
    void mapToAuthorReadOnlyDTO_shouldMapBooks() {
        Book book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        author.addBook(book);

        AuthorReadOnlyDTO dto = authorMapper.mapToAuthorReadOnlyDTO(author);

        assertThat(dto.bookReadOnlyDTOs()).hasSize(1);
        assertThat(dto.bookReadOnlyDTOs().iterator().next().title()).isEqualTo("Animal Farm");
    }

    @Test
    void mapToAuthorReadOnlyDTO_whenNoBooksExists_shouldReturnEmptySet() {
        AuthorReadOnlyDTO dto = authorMapper.mapToAuthorReadOnlyDTO(author);
        assertThat(dto.bookReadOnlyDTOs()).isEmpty();
    }
}