package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BookUpdateDTO(

        @NotNull
        UUID uuid,

        String language,

        @NotNull
        BigDecimal dailyCost,

        String description
) {
}
