package com.library.management.dto;

import java.util.UUID;

public record AddressReadOnlyDTO(UUID id, String street, String streetNumber,
                                 String city, String country, String postalCode) {
}
