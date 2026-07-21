package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RentalTest {

    private Rental rental;

    @BeforeEach
    void setUp() {
        rental = new Rental();
        rental.setRentalDate(Instant.now());
        rental.setDueDate(Instant.now().plusSeconds(86400 * 7));
    }

    @Test
    void isActive_whenReturnDateIsNull_shouldReturnTrue() {
        assertThat(rental.isActive()).isTrue();
    }

    @Test
    void isActive_whenReturnDateIsSet_shouldReturnFalse() {
        rental.setReturnDate(Instant.now());
        assertThat(rental.isActive()).isFalse();
    }

    @Test
    void softDelete_shouldMarkRentalAsDeleted() {
        rental.softDelete();
        assertThat(rental.isDeleted()).isTrue();
        assertThat(rental.getDeletedAt()).isNotNull();
    }

    @Test
    void equals_whenSameId_shouldReturnTrue() {
        Rental anotherRental = new Rental();
        anotherRental.setId(rental.getId());
        assertThat(rental).isEqualTo(anotherRental);
    }

    @Test
    void equals_whenDifferentId_shouldReturnFalse() {
        Rental anotherRental = new Rental();
        assertThat(rental).isNotEqualTo(anotherRental);
    }

    @Test
    void hashCode_shouldBeConsistent() {
        int hashCode1 = rental.hashCode();
        int hashCode2 = rental.hashCode();
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void equals_whenNull_shouldReturnFalse() {
        assertThat(rental.equals(null)).isFalse();
    }

    @Test
    void equals_whenDifferentType_shouldReturnFalse() {
        assertThat(rental.equals("string")).isFalse();
    }
}