package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record BookInsertDTO(

        @NotBlank
        String title,

        @NotBlank
        String isbn,

        LocalDate publishedDate,

        String language,

        @NotNull
        BigDecimal dailyCost,

        String description,

        Set<UUID> authorUuids
) {
}
