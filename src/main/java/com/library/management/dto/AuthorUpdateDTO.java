package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AuthorUpdateDTO(

        @NotBlank
        String firstname,

        @NotBlank
        String lastname,

        LocalDate birthDate,

        String birthPlace,

        String bio
) {
}
