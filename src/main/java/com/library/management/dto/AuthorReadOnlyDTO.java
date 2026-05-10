package com.library.management.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record AuthorReadOnlyDTO(UUID id, String firstname, String lastname,
                                LocalDate birthDate, String birthPlace, String bio,
                                Set<BookReadOnlyDTO> bookReadOnlyDTOs
)
{}
