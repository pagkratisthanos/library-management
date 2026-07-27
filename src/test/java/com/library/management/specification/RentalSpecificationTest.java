package com.library.management.specification;

import com.library.management.core.filters.RentalFilters;
import com.library.management.model.*;
import com.library.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RentalSpecificationTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private BookRepository bookRepository;

    private Member member;
    private Copy copy;
    private Rental activeRental;
    private Rental returnedRental;

    @BeforeEach
    void setUp() {
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
        member.setBirthDate(LocalDate.of(1990, 1, 1));
        member.setMembershipDate(LocalDate.of(2024, 1, 1));
        member.setAddress(address);
        memberRepository.save(member);

        Book book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);

        copy = new Copy();
        copy.setBook(book);
        copy.setAvailable(false);
        copy.setCondition(CopyCondition.NEW);
        copyRepository.save(copy);

        activeRental = new Rental();
        activeRental.setMember(member);
        activeRental.setCopy(copy);
        activeRental.setRentalDate(Instant.now());
        activeRental.setDueDate(Instant.now().plusSeconds(86400 * 7));
        rentalRepository.save(activeRental);

        returnedRental = new Rental();
        returnedRental.setMember(member);
        returnedRental.setCopy(copy);
        returnedRental.setRentalDate(Instant.now());
        returnedRental.setDueDate(Instant.now().plusSeconds(86400 * 7));
        returnedRental.setReturnDate(Instant.now());
        rentalRepository.save(returnedRental);
    }

    @Test
    void build_whenNoFilters_shouldReturnAllRentals() {
        RentalFilters filters = new RentalFilters();
        Page<Rental> result = rentalRepository.findAll(RentalSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void build_whenFilterByMemberUuid_shouldReturnMemberRentals() {
        RentalFilters filters = new RentalFilters();
        filters.setMemberUuid(member.getId());
        Page<Rental> result = rentalRepository.findAll(RentalSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void build_whenFilterByCopyUuid_shouldReturnCopyRentals() {
        RentalFilters filters = new RentalFilters();
        filters.setCopyUuid(copy.getId());
        Page<Rental> result = rentalRepository.findAll(RentalSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void build_whenFilterByActiveTrue_shouldReturnActiveRentals() {
        RentalFilters filters = new RentalFilters();
        filters.setActive("true");
        Page<Rental> result = rentalRepository.findAll(RentalSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getReturnDate()).isNull();
    }

    @Test
    void build_whenFilterByActiveFalse_shouldReturnReturnedRentals() {
        RentalFilters filters = new RentalFilters();
        filters.setActive("false");
        Page<Rental> result = rentalRepository.findAll(RentalSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getReturnDate()).isNotNull();
    }
}