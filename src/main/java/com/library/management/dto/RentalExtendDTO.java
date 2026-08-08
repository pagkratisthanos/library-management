package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record RentalExtendDTO(

        @NotNull
        Instant dueDate
) {}