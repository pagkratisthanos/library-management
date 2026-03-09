package com.library.management.dto;

public record AddressReadOnlyDTO(String street, String streetNumber, String city,
                                 String country, String postalCode) {
}
