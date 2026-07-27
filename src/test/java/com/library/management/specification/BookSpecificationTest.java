package com.library.management.specification;

import com.library.management.core.filters.BookFilters;
import com.library.management.model.Book;
import com.library.management.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookSpecificationTest {

    @Autowired
    private BookRepository bookRepository;

    private Book animalFarm;
    private Book nineteenEightyFour;

    @BeforeEach
    void setUp() {
        animalFarm = new Book();
        animalFarm.setTitle("Animal Farm");
        animalFarm.setIsbn("978-0-452-28424-4");
        animalFarm.setLanguage("English");
        animalFarm.setDailyCost(BigDecimal.valueOf(1.20));
        animalFarm.setDescription("A political allegory");
        animalFarm.setPublishedDate(LocalDate.of(1945, 8, 17));
        bookRepository.save(animalFarm);

        nineteenEightyFour = new Book();
        nineteenEightyFour.setTitle("1984");
        nineteenEightyFour.setIsbn("978-0-452-28423-4");
        nineteenEightyFour.setLanguage("Greek");
        nineteenEightyFour.setDailyCost(BigDecimal.valueOf(1.50));
        nineteenEightyFour.setDescription("A dystopian novel");
        nineteenEightyFour.setPublishedDate(LocalDate.of(1949, 6, 8));
        bookRepository.save(nineteenEightyFour);
    }

    @Test
    void build_whenNoFilters_shouldReturnAllActiveBooks() {
        BookFilters filters = new BookFilters();
        Page<Book> result = bookRepository.findAll(BookSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void build_whenFilterByTitle_shouldReturnMatchingBooks() {
        BookFilters filters = new BookFilters();
        filters.setTitle("Animal");
        Page<Book> result = bookRepository.findAll(BookSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Animal Farm");
    }

    @Test
    void build_whenFilterByIsbn_shouldReturnMatchingBooks() {
        BookFilters filters = new BookFilters();
        filters.setIsbn("978-0-452-28424-4");
        Page<Book> result = bookRepository.findAll(BookSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsbn()).isEqualTo("978-0-452-28424-4");
    }

    @Test
    void build_whenFilterByLanguage_shouldReturnMatchingBooks() {
        BookFilters filters = new BookFilters();
        filters.setLanguage("Greek");
        Page<Book> result = bookRepository.findAll(BookSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLanguage()).isEqualTo("Greek");
    }

    @Test
    void build_whenFilterByDescription_shouldReturnMatchingBooks() {
        BookFilters filters = new BookFilters();
        filters.setDescription("political");
        Page<Book> result = bookRepository.findAll(BookSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDescription()).isEqualTo("A political allegory");
    }

    @Test
    void build_whenDeletedBook_shouldNotReturn() {
        animalFarm.softDelete();
        bookRepository.save(animalFarm);

        BookFilters filters = new BookFilters();
        Page<Book> result = bookRepository.findAll(BookSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("1984");
    }
}