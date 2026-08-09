package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.core.filters.RentalFilters;
import com.library.management.dto.RentalExtendDTO;
import com.library.management.dto.RentalInsertDTO;
import com.library.management.model.*;
import com.library.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class RentalServiceTest {

    @Autowired
    private IRentalService rentalService;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private BookRepository bookRepository;

    private Member existingMember;
    private Copy existingCopy;
    private Rental existingRental;

    @BeforeEach
    void setUp() {
        Address address = new Address();
        address.setStreet("Ermou");
        address.setStreetNumber("10");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10563");

        existingMember = new Member();
        existingMember.setFirstname("Thanos");
        existingMember.setLastname("Pagkratis");
        existingMember.setEmail("thanos@example.com");
        existingMember.setPhoneNumber("6912345678");
        existingMember.setBirthDate(LocalDate.of(1990, 1, 1));
        existingMember.setMembershipDate(LocalDate.of(2024, 1, 1));
        existingMember.setAddress(address);
        memberRepository.save(existingMember);

        Book book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);

        existingCopy = new Copy();
        existingCopy.setBook(book);
        existingCopy.setAvailable(false);
        existingCopy.setCondition(CopyCondition.NEW);
        copyRepository.save(existingCopy);

        existingRental = new Rental();
        existingRental.setMember(existingMember);
        existingRental.setCopy(existingCopy);
        existingRental.setRentalDate(Instant.now());
        existingRental.setDueDate(Instant.now().plusSeconds(86400 * 7));
        rentalRepository.save(existingRental);
    }

    @Test
    void saveRental_whenValidData_shouldSaveAndReturnRental()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Copy availableCopy = new Copy();
        availableCopy.setBook(bookRepository.findAll().get(0));
        availableCopy.setAvailable(true);
        availableCopy.setCondition(CopyCondition.GOOD);
        copyRepository.save(availableCopy);

        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().plusSeconds(86400 * 7),
                existingMember.getId(),
                availableCopy.getId()
        );

        Rental saved = rentalService.saveRental(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMember().getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void saveRental_whenMemberNotFound_shouldThrowException() {
        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().plusSeconds(86400),
                UUID.randomUUID(),
                existingCopy.getId()
        );

        assertThatThrownBy(() -> rentalService.saveRental(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveRental_whenCopyNotFound_shouldThrowException() {
        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().plusSeconds(86400),
                existingMember.getId(),
                UUID.randomUUID()
        );

        assertThatThrownBy(() -> rentalService.saveRental(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveRental_whenCopyNotAvailable_shouldThrowException() {
        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().plusSeconds(86400),
                existingMember.getId(),
                existingCopy.getId()
        );

        assertThatThrownBy(() -> rentalService.saveRental(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void saveRental_whenDueDateInPast_shouldThrowException() {
        Copy availableCopy = new Copy();
        availableCopy.setBook(bookRepository.findAll().get(0));
        availableCopy.setAvailable(true);
        availableCopy.setCondition(CopyCondition.GOOD);
        copyRepository.save(availableCopy);

        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().minusSeconds(86400),
                existingMember.getId(),
                availableCopy.getId()
        );

        assertThatThrownBy(() -> rentalService.saveRental(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void saveRental_whenDueDateBeyondMaxPeriod_shouldThrowException() {
        Copy availableCopy = new Copy();
        availableCopy.setBook(bookRepository.findAll().get(0));
        availableCopy.setAvailable(true);
        availableCopy.setCondition(CopyCondition.NEW);
        copyRepository.save(availableCopy);

        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().plus(Duration.ofDays(120)),
                existingMember.getId(),
                availableCopy.getId()
        );

        assertThatThrownBy(() -> rentalService.saveRental(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void returnRental_whenActive_shouldSetReturnDateAndMakeCopyAvailable()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Rental returned = rentalService.returnRental(existingRental.getId());

        assertThat(returned.getReturnDate()).isNotNull();
        assertThat(returned.getCopy().getAvailable()).isTrue();
    }

    @Test
    void returnRental_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> rentalService.returnRental(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void returnRental_whenAlreadyReturned_shouldThrowException() {
        existingRental.setReturnDate(Instant.now());
        rentalRepository.save(existingRental);

        assertThatThrownBy(() -> rentalService.returnRental(existingRental.getId()))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void getRentalByUuid_whenExists_shouldReturnRental() throws EntityNotFoundException {
        Rental found = rentalService.getRentalByUuid(existingRental.getId());
        assertThat(found).isNotNull();
        assertThat(found.getMember().getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void getRentalByUuid_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> rentalService.getRentalByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getRentalsByMemberUuid_whenMemberExists_shouldReturnRentals() throws EntityNotFoundException {
        List<Rental> rentals = rentalService.getRentalsByMemberUuid(existingMember.getId());
        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getMember().getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void getRentalsByMemberUuid_whenMemberNotFound_shouldThrowException() {
        assertThatThrownBy(() -> rentalService.getRentalsByMemberUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getRentalsByCopyUuid_whenCopyExists_shouldReturnRentals() throws EntityNotFoundException {
        List<Rental> rentals = rentalService.getRentalsByCopyUuid(existingCopy.getId());
        assertThat(rentals).hasSize(1);
    }

    @Test
    void getRentalsByCopyUuid_whenCopyNotFound_shouldThrowException() {
        assertThatThrownBy(() -> rentalService.getRentalsByCopyUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getActiveRentalsPaginated_shouldReturnActiveRentals() {
        Page<Rental> activeRentals = rentalService.getActiveRentalsPaginated(PageRequest.of(0, 10));
        assertThat(activeRentals.getContent()).hasSize(1);
        assertThat(activeRentals.getContent().get(0).getReturnDate()).isNull();
    }

    @Test
    void getActiveRentalsPaginated_whenReturned_shouldReturnEmpty() {
        existingRental.setReturnDate(Instant.now());
        rentalRepository.save(existingRental);

        Page<Rental> activeRentals = rentalService.getActiveRentalsPaginated(PageRequest.of(0, 10));
        assertThat(activeRentals.getContent()).isEmpty();
    }

    @Test
    void getRentalsPaginated_shouldReturnAllRentals() {
        Page<Rental> rentals = rentalService.getRentalsPaginated(PageRequest.of(0, 10));
        assertThat(rentals.getContent()).hasSize(1);
    }

    @Test
    void getRentalsPaginatedFiltered_whenFilterByActive_shouldReturnActiveRentals() {
        RentalFilters filters = new RentalFilters();
        filters.setActive("true");

        Page<Rental> rentals = rentalService.getRentalsPaginatedFiltered(
                PageRequest.of(0, 10), filters);
        assertThat(rentals).isNotNull();
        assertThat(rentals.getContent()).hasSize(1);
        assertThat(rentals.getContent().get(0).getReturnDate()).isNull();
    }

    @Test
    void extendRental_whenValid_shouldMoveTheDueDateForward()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Instant newDueDate = Instant.now().plus(Duration.ofDays(14));

        Rental extended = rentalService.extendRental(
                existingRental.getId(), new RentalExtendDTO(newDueDate));

        assertThat(extended.getDueDate()).isEqualTo(newDueDate);
        assertThat(extended.isActive()).isTrue();
    }

    @Test
    void extendRental_whenNewDueDateIsEarlier_shouldThrowException() {
        Instant earlier = Instant.now().plus(Duration.ofDays(3));

        assertThatThrownBy(() -> rentalService.extendRental(
                existingRental.getId(), new RentalExtendDTO(earlier)))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void extendRental_whenNewDueDateEqualsTheCurrentOne_shouldThrowException() {
        Instant sameDate = existingRental.getDueDate();

        assertThatThrownBy(() -> rentalService.extendRental(
                existingRental.getId(), new RentalExtendDTO(sameDate)))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void extendRental_whenTotalPeriodExceedsTheLimit_shouldThrowException() {
        Instant tooFar = existingRental.getRentalDate().plus(Duration.ofDays(91));

        assertThatThrownBy(() -> rentalService.extendRental(
                existingRental.getId(), new RentalExtendDTO(tooFar)))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void extendRental_whenAtTheExactLimit_shouldSucceed()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Instant lastAllowed = existingRental.getRentalDate().plus(Duration.ofDays(90));

        Rental extended = rentalService.extendRental(
                existingRental.getId(), new RentalExtendDTO(lastAllowed));

        assertThat(extended.getDueDate()).isEqualTo(lastAllowed);
    }

    @Test
    void extendRental_whenAlreadyReturned_shouldThrowException()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        rentalService.returnRental(existingRental.getId());

        Instant newDueDate = Instant.now().plus(Duration.ofDays(14));

        assertThatThrownBy(() -> rentalService.extendRental(
                existingRental.getId(), new RentalExtendDTO(newDueDate)))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void extendRental_whenNotFound_shouldThrowException() {
        Instant newDueDate = Instant.now().plus(Duration.ofDays(14));

        assertThatThrownBy(() -> rentalService.extendRental(
                UUID.randomUUID(), new RentalExtendDTO(newDueDate)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    /** A rental that started a month ago and was due five days ago. */
    private Rental createOverdueRental() {
        Copy copy = new Copy();
        copy.setBook(bookRepository.findAll().get(0));
        copy.setAvailable(false);
        copy.setCondition(CopyCondition.GOOD);
        copyRepository.save(copy);

        Rental rental = new Rental();
        rental.setMember(existingMember);
        rental.setCopy(copy);
        rental.setRentalDate(Instant.now().minus(Duration.ofDays(30)));
        rental.setDueDate(Instant.now().minus(Duration.ofDays(5)));
        return rentalRepository.save(rental);
    }

    @Test
    void getOverdueRentalsPaginated_shouldReturnOnlyOverdueRentals() {
        Rental overdue = createOverdueRental();

        Page<Rental> result = rentalService.getOverdueRentalsPaginated(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(overdue.getId());
    }

    @Test
    void getOverdueRentalsPaginated_shouldIgnoreReturnedRentals()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Rental overdue = createOverdueRental();
        rentalService.returnRental(overdue.getId());

        Page<Rental> result = rentalService.getOverdueRentalsPaginated(PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void getOverdueRentalsPaginated_whenNothingIsOverdue_shouldReturnEmpty() {
        Page<Rental> result = rentalService.getOverdueRentalsPaginated(PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }
}