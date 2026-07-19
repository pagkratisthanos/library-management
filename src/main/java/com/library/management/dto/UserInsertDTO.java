package com.library.management.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserInsertDTO(

        @NotNull
        @Size(min = 3, max = 20)
        String username,

        @NotNull
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])^.{8,}$",
                message = "Password must be at least 8 characters and contain uppercase, lowercase, digit and special character")
        String password,

        @NotNull
        Long roleId
) {}