package com.library.management.repository;

import com.library.management.model.*;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RentalRepositoryTest {

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
    private Rental rental;

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

        rental = new Rental();
        rental.setMember(member);
        rental.setCopy(copy);
        rental.setRentalDate(Instant.now());
        rental.setDueDate(Instant.now().plusSeconds(86400 * 7));
        rentalRepository.save(rental);
    }

    @Test
    void findById_whenExists_shouldReturnRental() {
        Optional<Rental> found = rentalRepository.findById(rental.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getMember().getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        Optional<Rental> found = rentalRepository.findById(java.util.UUID.randomUUID());
        assertThat(found).isEmpty();
    }

    @Test
    void findByMember_Id_shouldReturnRentalsOfMember() {
        List<Rental> rentals = rentalRepository.findByMember_Id(member.getId());
        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getMember().getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void findByMember_Id_whenNoRentals_shouldReturnEmptyList() {
        Address address = new Address();
        address.setStreet("Stadiou");
        address.setStreetNumber("5");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10564");

        Member anotherMember = new Member();
        anotherMember.setFirstname("Another");
        anotherMember.setLastname("Member");
        anotherMember.setEmail("another@example.com");
        anotherMember.setPhoneNumber("6900000000");
        anotherMember.setBirthDate(LocalDate.of(1990, 1, 1));
        anotherMember.setMembershipDate(LocalDate.of(2024, 1, 1));
        anotherMember.setAddress(address);
        memberRepository.save(anotherMember);

        List<Rental> rentals = rentalRepository.findByMember_Id(anotherMember.getId());
        assertThat(rentals).isEmpty();
    }

    @Test
    void findByCopy_Id_shouldReturnRentalsOfCopy() {
        List<Rental> rentals = rentalRepository.findByCopy_Id(copy.getId());
        assertThat(rentals).hasSize(1);
    }

    @Test
    void findByCopy_Id_whenNoRentals_shouldReturnEmptyList() {
        Book anotherBook = new Book();
        anotherBook.setTitle("1984");
        anotherBook.setIsbn("978-0-452-28423-4");
        anotherBook.setLanguage("English");
        anotherBook.setDailyCost(BigDecimal.valueOf(1.50));
        bookRepository.save(anotherBook);

        Copy anotherCopy = new Copy();
        anotherCopy.setBook(anotherBook);
        anotherCopy.setAvailable(true);
        anotherCopy.setCondition(CopyCondition.GOOD);
        copyRepository.save(anotherCopy);

        List<Rental> rentals = rentalRepository.findByCopy_Id(anotherCopy.getId());
        assertThat(rentals).isEmpty();
    }

    @Test
    void findByReturnDateIsNull_shouldReturnActiveRentals() {
        Page<Rental> activeRentals = rentalRepository.findByReturnDateIsNull(PageRequest.of(0, 10));
        assertThat(activeRentals.getContent()).hasSize(1);
    }

    @Test
    void findByReturnDateIsNull_whenReturned_shouldReturnEmpty() {
        rental.setReturnDate(Instant.now());
        rentalRepository.save(rental);

        Page<Rental> activeRentals = rentalRepository.findByReturnDateIsNull(PageRequest.of(0, 10));
        assertThat(activeRentals.getContent()).isEmpty();
    }

    @Test
    void existsById_whenExists_shouldReturnTrue() {
        boolean exists = rentalRepository.existsById(rental.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_whenNotExists_shouldReturnFalse() {
        boolean exists = rentalRepository.existsById(java.util.UUID.randomUUID());
        assertThat(exists).isFalse();
    }
}