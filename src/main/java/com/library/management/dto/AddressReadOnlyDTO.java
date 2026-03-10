package com.library.management.dto;

import java.util.UUID;

public record AddressReadOnlyDTO(UUID uuid, String street, String streetNumber,
                                 String city, String country, String postalCode) {
}
