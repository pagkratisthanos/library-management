package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RentalUpdateDTO(

        @NotNull
        Instant dueDate,

        Instant returnDate
) {}