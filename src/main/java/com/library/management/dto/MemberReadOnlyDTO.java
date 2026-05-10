package com.library.management.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MemberReadOnlyDTO(
        UUID id,
        AddressReadOnlyDTO addressReadOnlyDTO,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        LocalDate membershipDate
) {}