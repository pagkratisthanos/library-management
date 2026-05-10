package com.library.management.dto;

import java.time.Instant;
import java.util.UUID;

public record RentalReadOnlyDTO(UUID id, UUID memberUuid, UUID copyUuid,
                                Instant rentalDate, Instant dueDate, Instant returnDate,
                                String memberFirstname, String memberLastname,
                                String bookTitle
) {
}
