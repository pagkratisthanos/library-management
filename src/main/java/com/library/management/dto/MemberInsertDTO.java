package com.library.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MemberInsertDTO(

        @Valid
        @NotNull
        AddressInsertDTO addressInsertDTO,

        @NotBlank
        @Size(min = 2)
        String firstname,

        @NotBlank
        @Size(min = 2)
        String lastname,

        @NotBlank
        @Pattern(regexp = "^(\\+30|0030)?[0-9]{10}$", message = "Invalid phone number")
        String phoneNumber,

        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Invalid email format")
        String email,

        LocalDate birthDate,

        @NotNull
        LocalDate membershipDate
) {}
