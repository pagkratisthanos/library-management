package com.library.management.repository;

import com.library.management.model.Book;
import com.library.management.model.Copy;
import com.library.management.model.CopyCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CopyRepositoryTest {

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private BookRepository bookRepository;

    private Book book;
    private Copy copy;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);

        copy = new Copy();
        copy.setBook(book);
        copy.setAvailable(true);
        copy.setCondition(CopyCondition.NEW);
        copyRepository.save(copy);
    }

    @Test
    void findById_whenExists_shouldReturnCopy() {
        Optional<Copy> found = copyRepository.findById(copy.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCondition()).isEqualTo(CopyCondition.NEW);
    }

    @Test
    void findByIdAndDeletedFalse_whenExists_shouldReturnCopy() {
        Optional<Copy> found = copyRepository.findByIdAndDeletedFalse(copy.getId());
        assertThat(found).isPresent();
    }

    @Test
    void findByIdAndDeletedFalse_whenDeleted_shouldReturnEmpty() {
        copy.softDelete();
        copyRepository.save(copy);

        Optional<Copy> found = copyRepository.findByIdAndDeletedFalse(copy.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findByDeletedFalse_shouldReturnOnlyActiveCopies() {
        Copy deletedCopy = new Copy();
        deletedCopy.setBook(book);
        deletedCopy.setAvailable(false);
        deletedCopy.setCondition(CopyCondition.POOR);
        deletedCopy.softDelete();
        copyRepository.save(deletedCopy);

        Page<Copy> copies = copyRepository.findByDeletedFalse(PageRequest.of(0, 10));
        assertThat(copies.getContent()).hasSize(1);
        assertThat(copies.getContent().get(0).getCondition()).isEqualTo(CopyCondition.NEW);
    }

    @Test
    void existsById_whenExists_shouldReturnTrue() {
        boolean exists = copyRepository.existsById(copy.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_whenNotExists_shouldReturnFalse() {
        boolean exists = copyRepository.existsById(java.util.UUID.randomUUID());
        assertThat(exists).isFalse();
    }

    @Test
    void findByBookId_shouldReturnCopiesOfBook() {
        List<Copy> copies = copyRepository.findByBookId(book.getId());
        assertThat(copies).hasSize(1);
        assertThat(copies.get(0).getCondition()).isEqualTo(CopyCondition.NEW);
    }

    @Test
    void findByBookId_whenNoCopies_shouldReturnEmptyList() {
        Book anotherBook = new Book();
        anotherBook.setTitle("1984");
        anotherBook.setIsbn("978-0-452-28423-4");
        anotherBook.setLanguage("English");
        anotherBook.setDailyCost(BigDecimal.valueOf(1.50));
        bookRepository.save(anotherBook);

        List<Copy> copies = copyRepository.findByBookId(anotherBook.getId());
        assertThat(copies).isEmpty();
    }

    @Test
    void findByBookIdAndAvailableTrue_shouldReturnAvailableCopies() {
        List<Copy> copies = copyRepository.findByBookIdAndAvailableTrue(book.getId());
        assertThat(copies).hasSize(1);
    }

    @Test
    void findByBookIdAndAvailableTrue_whenNotAvailable_shouldReturnEmptyList() {
        copy.setAvailable(false);
        copyRepository.save(copy);

        List<Copy> copies = copyRepository.findByBookIdAndAvailableTrue(book.getId());
        assertThat(copies).isEmpty();
    }

    @Test
    void findByAvailableTrueAndDeletedFalse_shouldReturnAvailableAndActiveCopies() {
        Page<Copy> copies = copyRepository.findByAvailableTrueAndDeletedFalse(PageRequest.of(0, 10));
        assertThat(copies.getContent()).hasSize(1);
    }

    @Test
    void findByAvailableTrueAndDeletedFalse_whenDeleted_shouldReturnEmpty() {
        copy.softDelete();
        copyRepository.save(copy);

        Page<Copy> copies = copyRepository.findByAvailableTrueAndDeletedFalse(PageRequest.of(0, 10));
        assertThat(copies.getContent()).isEmpty();
    }

    @Test
    void countByBook_Id_shouldReturnCorrectCount() {
        Copy anotherCopy = new Copy();
        anotherCopy.setBook(book);
        anotherCopy.setAvailable(true);
        anotherCopy.setCondition(CopyCondition.GOOD);
        copyRepository.save(anotherCopy);

        long count = copyRepository.countByBook_Id(book.getId());
        assertThat(count).isEqualTo(2);
    }
}