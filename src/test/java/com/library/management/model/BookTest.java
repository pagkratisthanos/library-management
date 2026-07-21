package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    private Book book;
    private Author author;
    private Copy copy;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));

        author = new Author();
        author.setFirstname("George");
        author.setLastname("Orwell");

        copy = new Copy();
        copy.setAvailable(true);
        copy.setCondition(CopyCondition.NEW);
    }

    @Test
    void addAuthor_shouldAddAuthorToBook() {
        book.addAuthor(author);
        assertThat(book.getAllAuthors()).contains(author);
    }

    @Test
    void removeAuthor_shouldRemoveAuthorFromBook() {
        book.addAuthor(author);
        book.removeAuthor(author);
        assertThat(book.getAllAuthors()).doesNotContain(author);
    }

    @Test
    void getAuthor_whenExists_shouldReturnAuthor() {
        book.addAuthor(author);
        assertThat(book.getAuthor(author.getId())).isPresent();
    }

    @Test
    void getAuthor_whenNotExists_shouldReturnEmpty() {
        assertThat(book.getAuthor(UUID.randomUUID())).isEmpty();
    }

    @Test
    void addCopy_shouldAddCopyToBook() {
        book.addCopy(copy);
        assertThat(book.getAllCopies()).contains(copy);
    }

    @Test
    void getCopy_whenExists_shouldReturnCopy() {
        book.addCopy(copy);
        assertThat(book.getCopy(copy.getId())).isPresent();
    }

    @Test
    void getCopy_whenNotExists_shouldReturnEmpty() {
        assertThat(book.getCopy(UUID.randomUUID())).isEmpty();
    }

    @Test
    void getAllCopies_shouldReturnUnmodifiableList() {
        book.addCopy(copy);
        assertThat(book.getAllCopies()).hasSize(1);
    }

    @Test
    void getAllAuthors_shouldReturnUnmodifiableSet() {
        book.addAuthor(author);
        assertThat(book.getAllAuthors()).hasSize(1);
    }

    @Test
    void softDelete_shouldMarkBookAsDeleted() {
        book.softDelete();
        assertThat(book.isDeleted()).isTrue();
        assertThat(book.getDeletedAt()).isNotNull();
    }

    @Test
    void equals_whenSameId_shouldReturnTrue() {
        Book anotherBook = new Book();
        anotherBook.setId(book.getId());
        assertThat(book).isEqualTo(anotherBook);
    }

    @Test
    void equals_whenDifferentId_shouldReturnFalse() {
        Book anotherBook = new Book();
        assertThat(book).isNotEqualTo(anotherBook);
    }
}