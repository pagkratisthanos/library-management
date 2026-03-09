package com.library.management.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MemberReadOnlyDTO(UUID uuid, AddressReadOnlyDTO addressReadOnlyDTO,
                                String firstname, String lastname,
                                String phoneNumber,
                                String email,
                                LocalDate birthDate,
                                LocalDate membershipDate
                                ) {
}
