package com.library.management.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    private Member member;
    private Address address;
    private Rental rental;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setStreet("Ermou");
        address.setStreetNumber("10");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10563");

        member = new Member();
        member.setFirstname("Thanos");
        member.setLastname("Pagkratis");
        member.setEmail("thanos@example.com");
        member.setPhoneNumber("6912345678");
        member.setBirthDate(LocalDate.of(1990, 1, 1));
        member.setMembershipDate(LocalDate.of(2024, 1, 1));
        member.setAddress(address);

        rental = new Rental();
    }

    @Test
    void addRental_shouldAddRentalToMember() {
        member.addRental(rental);
        assertThat(member.getAllRentals()).contains(rental);
    }

    @Test
    void getAllRentals_shouldReturnUnmodifiableList() {
        member.addRental(rental);
        assertThat(member.getAllRentals()).hasSize(1);
    }

    @Test
    void softDelete_shouldMarkMemberAsDeleted() {
        member.softDelete();
        assertThat(member.isDeleted()).isTrue();
        assertThat(member.getDeletedAt()).isNotNull();
    }

    @Test
    void address_shouldBeSet() {
        assertThat(member.getAddress()).isNotNull();
        assertThat(member.getAddress().getCity()).isEqualTo("Athens");
    }

    @Test
    void equals_whenSameId_shouldReturnTrue() {
        Member anotherMember = new Member();
        anotherMember.setId(member.getId());
        assertThat(member).isEqualTo(anotherMember);
    }

    @Test
    void equals_whenDifferentId_shouldReturnFalse() {
        Member anotherMember = new Member();
        assertThat(member).isNotEqualTo(anotherMember);
    }
}