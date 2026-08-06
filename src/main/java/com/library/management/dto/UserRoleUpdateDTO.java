package com.library.management.dto;

import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateDTO(

        @NotNull
        Long roleId
) {}