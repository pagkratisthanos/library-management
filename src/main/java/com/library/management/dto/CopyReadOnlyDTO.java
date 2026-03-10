package com.library.management.dto;

import java.util.UUID;

public record CopyReadOnlyDTO(UUID uuid, UUID bookUuid,
                              String bookTitle, Boolean available,
                              String condition
                              ) {
}
