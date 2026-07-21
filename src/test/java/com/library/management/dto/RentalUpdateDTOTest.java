package com.library.management.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RentalUpdateDTOTest {

    @Test
    void rentalUpdateDTO_shouldCreateCorrectly() {
        Instant dueDate = Instant.now().plusSeconds(86400);
        Instant returnDate = Instant.now();

        RentalUpdateDTO dto = new RentalUpdateDTO(dueDate, returnDate);

        assertThat(dto.dueDate()).isEqualTo(dueDate);
        assertThat(dto.returnDate()).isEqualTo(returnDate);
    }

    @Test
    void rentalUpdateDTO_whenReturnDateNull_shouldCreateCorrectly() {
        Instant dueDate = Instant.now().plusSeconds(86400);

        RentalUpdateDTO dto = new RentalUpdateDTO(dueDate, null);

        assertThat(dto.dueDate()).isEqualTo(dueDate);
        assertThat(dto.returnDate()).isNull();
    }
}