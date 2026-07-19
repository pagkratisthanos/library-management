package com.library.management.dto;

import java.util.UUID;

public record UserReadOnlyDTO(
        UUID id,
        String username,
        String role
) {}