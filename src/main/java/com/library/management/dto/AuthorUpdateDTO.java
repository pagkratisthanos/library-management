package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record AuthorUpdateDTO(

        @NotNull
        UUID uuid,

        @NotBlank
        String firstname,

        @NotBlank
        String lastname,

        String bio


) {
}
