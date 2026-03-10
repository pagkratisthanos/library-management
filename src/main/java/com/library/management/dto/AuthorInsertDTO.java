package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AuthorInsertDTO(

        @NotBlank
        String firstname,

        @NotBlank
        String lastname,

        @NotNull
        LocalDate birthDate,

        String birthPlace,

        String bio
) { }
