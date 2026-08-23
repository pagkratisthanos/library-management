package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record AuthorUpdateDTO(

        @NotBlank
        String firstname,

        @NotBlank
        String lastname,

        @PastOrPresent(message = "Birth date cannot be in the future")
        LocalDate birthDate,

        String birthPlace,

        String bio
) {
}
