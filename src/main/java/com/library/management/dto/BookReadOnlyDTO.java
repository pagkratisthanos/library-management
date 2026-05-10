package com.library.management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BookReadOnlyDTO(UUID id, String title, String isbn,
                              LocalDate publishedDate, String language,
                              BigDecimal dailyCost, String description,
                              Set<AuthorReadOnlyDTO> authorReadOnlyDTOs
                              ) {
}
