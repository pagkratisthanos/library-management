package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setStreet("Ermou");
        address.setStreetNumber("10");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10563");
    }

    @Test
    void fields_shouldBeSetCorrectly() {
        assertThat(address.getStreet()).isEqualTo("Ermou");
        assertThat(address.getStreetNumber()).isEqualTo("10");
        assertThat(address.getCity()).isEqualTo("Athens");
        assertThat(address.getCountry()).isEqualTo("Greece");
        assertThat(address.getPostalCode()).isEqualTo("10563");
    }

    @Test
    void softDelete_shouldMarkAddressAsDeleted() {
        address.softDelete();
        assertThat(address.isDeleted()).isTrue();
        assertThat(address.getDeletedAt()).isNotNull();
    }

    @Test
    void equals_whenSameId_shouldReturnTrue() {
        Address anotherAddress = new Address();
        anotherAddress.setId(address.getId());
        assertThat(address).isEqualTo(anotherAddress);
    }

    @Test
    void equals_whenDifferentId_shouldReturnFalse() {
        Address anotherAddress = new Address();
        assertThat(address).isNotEqualTo(anotherAddress);
    }

    @Test
    void id_shouldBeGeneratedAutomatically() {
        assertThat(address.getId()).isNotNull();
    }

    @Test
    void hashCode_shouldBeConsistent() {
        int hashCode1 = address.hashCode();
        int hashCode2 = address.hashCode();
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    void equals_whenNull_shouldReturnFalse() {
        assertThat(address.equals(null)).isFalse();
    }

    @Test
    void equals_whenDifferentType_shouldReturnFalse() {
        assertThat(address.equals("string")).isFalse();
    }
}