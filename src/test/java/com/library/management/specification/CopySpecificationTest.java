package com.library.management.specification;

import com.library.management.core.filters.CopyFilters;
import com.library.management.model.Book;
import com.library.management.model.Copy;
import com.library.management.model.CopyCondition;
import com.library.management.repository.BookRepository;
import com.library.management.repository.CopyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CopySpecificationTest {

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private BookRepository bookRepository;

    private Copy availableCopy;
    private Copy unavailableCopy;

    @BeforeEach
    void setUp() {
        Book book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);

        availableCopy = new Copy();
        availableCopy.setBook(book);
        availableCopy.setAvailable(true);
        availableCopy.setCondition(CopyCondition.NEW);
        copyRepository.save(availableCopy);

        unavailableCopy = new Copy();
        unavailableCopy.setBook(book);
        unavailableCopy.setAvailable(false);
        unavailableCopy.setCondition(CopyCondition.GOOD);
        copyRepository.save(unavailableCopy);
    }

    @Test
    void build_whenNoFilters_shouldReturnAllActiveCopies() {
        CopyFilters filters = new CopyFilters();
        Page<Copy> result = copyRepository.findAll(CopySpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void build_whenFilterByAvailableTrue_shouldReturnAvailableCopies() {
        CopyFilters filters = new CopyFilters();
        filters.setAvailable(true);
        Page<Copy> result = copyRepository.findAll(CopySpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAvailable()).isTrue();
    }

    @Test
    void build_whenFilterByAvailableFalse_shouldReturnUnavailableCopies() {
        CopyFilters filters = new CopyFilters();
        filters.setAvailable(false);
        Page<Copy> result = copyRepository.findAll(CopySpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAvailable()).isFalse();
    }

    @Test
    void build_whenFilterByCondition_shouldReturnMatchingCopies() {
        CopyFilters filters = new CopyFilters();
        filters.setCondition(CopyCondition.NEW);
        Page<Copy> result = copyRepository.findAll(CopySpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCondition()).isEqualTo(CopyCondition.NEW);
    }

    @Test
    void build_whenDeletedCopy_shouldNotReturn() {
        availableCopy.softDelete();
        copyRepository.save(availableCopy);

        CopyFilters filters = new CopyFilters();
        Page<Copy> result = copyRepository.findAll(CopySpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCondition()).isEqualTo(CopyCondition.GOOD);
    }
}