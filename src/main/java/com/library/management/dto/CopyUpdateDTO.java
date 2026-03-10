package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CopyUpdateDTO(

        @NotNull
        UUID uuid,

        @NotNull
        Boolean available,

        @NotBlank
        String condition
) {}
