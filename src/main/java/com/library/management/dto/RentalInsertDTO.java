package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RentalInsertDTO(

        @NotNull
        Instant dueDate,

        @NotNull
        UUID memberUuid,

        @NotNull
        UUID copyUuid
) {
}
