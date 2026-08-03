package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BookUpdateDTO(

        @NotBlank
        String title,

        @NotBlank
        String isbn,

        LocalDate publishedDate,

        String language,

        @NotNull
        BigDecimal dailyCost,

        String description
) {
}