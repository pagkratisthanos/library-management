package com.library.management.dto;

import com.library.management.model.CopyCondition;

import java.util.UUID;

public record CopyReadOnlyDTO(UUID id, UUID bookUuid,
                              String bookTitle, Boolean available,
                              CopyCondition condition
                              ) {
}
