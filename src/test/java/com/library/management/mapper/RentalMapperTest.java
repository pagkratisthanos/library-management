package com.library.management.mapper;

import com.library.management.dto.RentalInsertDTO;
import com.library.management.dto.RentalReadOnlyDTO;
import com.library.management.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RentalMapperTest {

    private RentalMapper rentalMapper;
    private Rental rental;
    private Member member;
    private Copy copy;
    private Book book;

    @BeforeEach
    void setUp() {
        rentalMapper = new RentalMapper();

        book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));

        copy = new Copy();
        copy.setBook(book);
        copy.setAvailable(false);
        copy.setCondition(CopyCondition.NEW);

        Address address = new Address();
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
        member.setAddress(address);

        rental = new Rental();
        rental.setMember(member);
        rental.setCopy(copy);
        rental.setRentalDate(Instant.now());
        rental.setDueDate(Instant.now().plusSeconds(86400 * 7));
    }

    @Test
    void mapToRentalEntity_shouldMapDueDate() {
        Instant dueDate = Instant.now().plusSeconds(86400 * 7);
        RentalInsertDTO dto = new RentalInsertDTO(dueDate, member.getId(), copy.getId());

        Rental mapped = rentalMapper.mapToRentalEntity(dto);

        assertThat(mapped.getDueDate()).isEqualTo(dueDate);
    }

    @Test
    void mapToRentalReadOnlyDTO_shouldMapCorrectly() {
        RentalReadOnlyDTO dto = rentalMapper.mapToRentalReadOnlyDTO(rental);

        assertThat(dto.memberUuid()).isEqualTo(member.getId());
        assertThat(dto.copyUuid()).isEqualTo(copy.getId());
        assertThat(dto.memberFirstname()).isEqualTo("Thanos");
        assertThat(dto.memberLastname()).isEqualTo("Pagkratis");
        assertThat(dto.bookTitle()).isEqualTo("Animal Farm");
        assertThat(dto.returnDate()).isNull();
    }

    @Test
    void mapToRentalReadOnlyDTO_whenReturned_shouldMapReturnDate() {
        Instant returnDate = Instant.now();
        rental.setReturnDate(returnDate);

        RentalReadOnlyDTO dto = rentalMapper.mapToRentalReadOnlyDTO(rental);

        assertThat(dto.returnDate()).isEqualTo(returnDate);
    }
}