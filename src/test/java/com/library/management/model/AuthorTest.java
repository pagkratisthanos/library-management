package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorTest {

    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setFirstname("George");
        author.setLastname("Orwell");
        author.setBirthDate(LocalDate.of(1903, 6, 25));

        book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
    }

    @Test
    void addBook_shouldAddBookToAuthor() {
        author.addBook(book);
        assertThat(author.getAllBooks()).contains(book);
    }

    @Test
    void removeBook_shouldRemoveBookFromAuthor() {
        author.addBook(book);
        author.removeBook(book);
        assertThat(author.getAllBooks()).doesNotContain(book);
    }

    @Test
    void getAllBooks_shouldReturnUnmodifiableSet() {
        author.addBook(book);
        assertThat(author.getAllBooks()).hasSize(1);
    }

    @Test
    void getBook_whenExists_shouldReturnBook() {
        author.addBook(book);
        assertThat(author.getBook(book.getId())).isPresent();
    }

    @Test
    void getBook_whenNotExists_shouldReturnEmpty() {
        assertThat(author.getBook(UUID.randomUUID())).isEmpty();
    }

    @Test
    void softDelete_shouldMarkAuthorAsDeleted() {
        author.softDelete();
        assertThat(author.isDeleted()).isTrue();
        assertThat(author.getDeletedAt()).isNotNull();
    }

    @Test
    void equals_whenSameId_shouldReturnTrue() {
        Author anotherAuthor = new Author();
        anotherAuthor.setId(author.getId());
        assertThat(author).isEqualTo(anotherAuthor);
    }

    @Test
    void equals_whenDifferentId_shouldReturnFalse() {
        Author anotherAuthor = new Author();
        assertThat(author).isNotEqualTo(anotherAuthor);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        int hashCode1 = author.hashCode();
        int hashCode2 = author.hashCode();
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void equals_whenNull_shouldReturnFalse() {
        assertThat(author.equals(null)).isFalse();
    }

    @Test
    void equals_whenDifferentType_shouldReturnFalse() {
        assertThat(author.equals("string")).isFalse();
    }
}