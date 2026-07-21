package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CopyTest {

    private Copy copy;
    private Book book;
    private Rental rental;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setTitle("Animal Farm");

        copy = new Copy();
        copy.setBook(book);
        copy.setAvailable(true);
        copy.setCondition(CopyCondition.NEW);

        rental = new Rental();
    }

    @Test
    void addRental_shouldAddRentalToCopy() {
        copy.addRental(rental);
        assertThat(copy.getAllRentals()).contains(rental);
    }

    @Test
    void getAllRentals_shouldReturnUnmodifiableList() {
        copy.addRental(rental);
        assertThat(copy.getAllRentals()).hasSize(1);
    }

    @Test
    void softDelete_shouldMarkCopyAsDeleted() {
        copy.softDelete();
        assertThat(copy.isDeleted()).isTrue();
        assertThat(copy.getDeletedAt()).isNotNull();
    }

    @Test
    void available_shouldBeTrue() {
        assertThat(copy.getAvailable()).isTrue();
    }

    @Test
    void condition_shouldBeNew() {
        assertThat(copy.getCondition()).isEqualTo(CopyCondition.NEW);
    }

    @Test
    void equals_whenSameId_shouldReturnTrue() {
        Copy anotherCopy = new Copy();
        anotherCopy.setId(copy.getId());
        assertThat(copy).isEqualTo(anotherCopy);
    }

    @Test
    void equals_whenDifferentId_shouldReturnFalse() {
        Copy anotherCopy = new Copy();
        assertThat(copy).isNotEqualTo(anotherCopy);
    }
}