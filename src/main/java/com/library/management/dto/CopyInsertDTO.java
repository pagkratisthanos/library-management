package com.library.management.dto;

import com.library.management.model.CopyCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CopyInsertDTO(

        @NotNull
        UUID bookUuid,

        @NotNull
        Boolean available,

        @NotBlank
        CopyCondition condition
) {
}
