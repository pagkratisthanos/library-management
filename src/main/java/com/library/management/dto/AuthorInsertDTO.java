package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AuthorInsertDTO(

        @NotBlank
        String firstname,

        @NotBlank
        String lastname,

        @NotNull
        @PastOrPresent(message = "Birth date cannot be in the future")
        LocalDate birthDate,

        String birthPlace,

        String bio
) { }
