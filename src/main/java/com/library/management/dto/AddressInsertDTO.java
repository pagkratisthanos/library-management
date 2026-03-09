package com.library.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AddressInsertDTO(


        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "Street number is required")
        String streetNumber,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "Postal code is required")
        String postalCode
) {
}
