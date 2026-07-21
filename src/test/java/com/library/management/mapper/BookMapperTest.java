package com.library.management.mapper;

import com.library.management.dto.BookInsertDTO;
import com.library.management.dto.BookReadOnlyDTO;
import com.library.management.model.Author;
import com.library.management.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BookMapperTest {

    private BookMapper bookMapper;
    private Book book;

    @BeforeEach
    void setUp() {
        bookMapper = new BookMapper();

        book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        book.setPublishedDate(LocalDate.of(1945, 8, 17));
        book.setDescription("A political allegory");
    }

    @Test
    void mapToBookEntity_shouldMapCorrectly() {
        BookInsertDTO dto = new BookInsertDTO(
                "Animal Farm", "978-0-452-28424-4",
                LocalDate.of(1945, 8, 17), "English",
                BigDecimal.valueOf(1.20), "A political allegory", null
        );

        Book mapped = bookMapper.mapToBookEntity(dto);

        assertThat(mapped.getTitle()).isEqualTo("Animal Farm");
        assertThat(mapped.getIsbn()).isEqualTo("978-0-452-28424-4");
        assertThat(mapped.getLanguage()).isEqualTo("English");
        assertThat(mapped.getDailyCost()).isEqualTo(BigDecimal.valueOf(1.20));
        assertThat(mapped.getPublishedDate()).isEqualTo(LocalDate.of(1945, 8, 17));
        assertThat(mapped.getDescription()).isEqualTo("A political allegory");
    }

    @Test
    void mapToBookReadOnlyDTO_shouldMapCorrectly() {
        BookReadOnlyDTO dto = bookMapper.mapToBookReadOnlyDTO(book);

        assertThat(dto.title()).isEqualTo("Animal Farm");
        assertThat(dto.isbn()).isEqualTo("978-0-452-28424-4");
        assertThat(dto.language()).isEqualTo("English");
        assertThat(dto.dailyCost()).isEqualTo(BigDecimal.valueOf(1.20));
        assertThat(dto.publishedDate()).isEqualTo(LocalDate.of(1945, 8, 17));
        assertThat(dto.description()).isEqualTo("A political allegory");
    }

    @Test
    void mapToBookReadOnlyDTO_shouldMapAuthors() {
        Author author = new Author();
        author.setFirstname("George");
        author.setLastname("Orwell");
        book.addAuthor(author);

        BookReadOnlyDTO dto = bookMapper.mapToBookReadOnlyDTO(book);

        assertThat(dto.authorReadOnlyDTOs()).hasSize(1);
        assertThat(dto.authorReadOnlyDTOs().iterator().next().firstname()).isEqualTo("George");
    }

    @Test
    void mapToBookReadOnlyDTO_whenNoAuthors_shouldReturnEmptySet() {
        BookReadOnlyDTO dto = bookMapper.mapToBookReadOnlyDTO(book);
        assertThat(dto.authorReadOnlyDTOs()).isEmpty();
    }
}