package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.CopyInsertDTO;
import com.library.management.dto.CopyUpdateDTO;
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
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class CopyServiceTest {

    @Autowired
    private ICopyService copyService;

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RentalRepository rentalRepository;

    private Book existingBook;
    private Copy existingCopy;

    @BeforeEach
    void setUp() {
        existingBook = new Book();
        existingBook.setTitle("Animal Farm");
        existingBook.setIsbn("978-0-452-28424-4");
        existingBook.setLanguage("English");
        existingBook.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(existingBook);

        existingCopy = new Copy();
        existingCopy.setBook(existingBook);
        existingCopy.setAvailable(true);
        existingCopy.setCondition(CopyCondition.NEW);
        copyRepository.save(existingCopy);
    }

    @Test
    void saveCopy_whenValidData_shouldSaveAndReturnCopy()
            throws EntityInvalidArgumentException, EntityNotFoundException {
        CopyInsertDTO dto = new CopyInsertDTO(existingBook.getId(), true, CopyCondition.GOOD);

        Copy saved = copyService.saveCopy(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCondition()).isEqualTo(CopyCondition.GOOD);
        assertThat(saved.getAvailable()).isTrue();
    }

    @Test
    void saveCopy_whenBookNotFound_shouldThrowException() {
        CopyInsertDTO dto = new CopyInsertDTO(UUID.randomUUID(), true, CopyCondition.NEW);

        assertThatThrownBy(() -> copyService.saveCopy(dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveCopy_whenBookDeleted_shouldThrowException() {
        existingBook.softDelete();
        bookRepository.save(existingBook);

        CopyInsertDTO dto = new CopyInsertDTO(existingBook.getId(), true, CopyCondition.NEW);

        assertThatThrownBy(() -> copyService.saveCopy(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void updateCopy_whenValidData_shouldUpdateAndReturn()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        CopyUpdateDTO dto = new CopyUpdateDTO(true, CopyCondition.GOOD);

        Copy updated = copyService.updateCopy(existingCopy.getId(), dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getCondition()).isEqualTo(CopyCondition.GOOD);
    }

    @Test
    void updateCopy_whenNotFound_shouldThrowException() {
        CopyUpdateDTO dto = new CopyUpdateDTO(true, CopyCondition.GOOD);

        assertThatThrownBy(() -> copyService.updateCopy(UUID.randomUUID(), dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateCopy_whenSetAvailableWithActiveRental_shouldThrowException() {
        existingCopy.setAvailable(false);
        copyRepository.save(existingCopy);

        Address address = new Address();
        address.setStreet("Ermou");
        address.setStreetNumber("10");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10563");

        Member member = new Member();
        member.setFirstname("Thanos");
        member.setLastname("Pagkratis");
        member.setEmail("thanos@example.com");
        member.setPhoneNumber("6912345678");
        member.setBirthDate(LocalDate.of(1990, 1, 1));
        member.setMembershipDate(LocalDate.of(2024, 1, 1));
        member.setAddress(address);
        memberRepository.save(member);

        Rental rental = new Rental();
        rental.setMember(member);
        rental.setCopy(existingCopy);
        rental.setRentalDate(Instant.now());
        rental.setDueDate(Instant.now().plusSeconds(86400));
        rentalRepository.save(rental);
        existingCopy.addRental(rental);
        copyRepository.save(existingCopy);

        CopyUpdateDTO dto = new CopyUpdateDTO(true, CopyCondition.GOOD);

        assertThatThrownBy(() -> copyService.updateCopy(existingCopy.getId(), dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void deleteCopyByUuid_whenNoActiveRentals_shouldSoftDelete()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        copyService.deleteCopyByUuid(existingCopy.getId());

        Copy deleted = copyRepository.findById(existingCopy.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
        assertThat(deleted.getDeletedAt()).isNotNull();
    }

    @Test
    void deleteCopyByUuid_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> copyService.deleteCopyByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteCopyByUuid_whenHasActiveRental_shouldThrowException() {
        existingCopy.setAvailable(false);
        copyRepository.save(existingCopy);

        Address address = new Address();
        address.setStreet("Ermou");
        address.setStreetNumber("10");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10563");

        Member member = new Member();
        member.setFirstname("Thanos");
        member.setLastname("Pagkratis");
        member.setEmail("thanos@example.com");
        member.setPhoneNumber("6912345678");
        member.setBirthDate(LocalDate.of(1990, 1, 1));
        member.setMembershipDate(LocalDate.of(2024, 1, 1));
        member.setAddress(address);
        memberRepository.save(member);

        Rental rental = new Rental();
        rental.setMember(member);
        rental.setCopy(existingCopy);
        rental.setRentalDate(Instant.now());
        rental.setDueDate(Instant.now().plusSeconds(86400));
        rentalRepository.save(rental);
        existingCopy.addRental(rental);
        copyRepository.save(existingCopy);

        assertThatThrownBy(() -> copyService.deleteCopyByUuid(existingCopy.getId()))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void getCopyByUuidDeletedFalse_whenExists_shouldReturnCopy() throws EntityNotFoundException {
        Copy found = copyService.getCopyByUuidDeletedFalse(existingCopy.getId());
        assertThat(found).isNotNull();
        assertThat(found.getCondition()).isEqualTo(CopyCondition.NEW);
    }

    @Test
    void getCopyByUuidDeletedFalse_whenDeleted_shouldThrowException() {
        existingCopy.softDelete();
        copyRepository.save(existingCopy);

        assertThatThrownBy(() -> copyService.getCopyByUuidDeletedFalse(existingCopy.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getCopyByUuidDeletedFalse_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> copyService.getCopyByUuidDeletedFalse(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getCopiesPaginatedAndDeletedFalse_shouldReturnOnlyActiveCopies() {
        existingCopy.softDelete();
        copyRepository.save(existingCopy);

        Copy activeCopy = new Copy();
        activeCopy.setBook(existingBook);
        activeCopy.setAvailable(true);
        activeCopy.setCondition(CopyCondition.GOOD);
        copyRepository.save(activeCopy);

        Page<Copy> copies = copyService.getCopiesPaginatedAndDeletedFalse(PageRequest.of(0, 10));
        assertThat(copies.getContent()).hasSize(1);
        assertThat(copies.getContent().get(0).getCondition()).isEqualTo(CopyCondition.GOOD);
    }

    @Test
    void getCopyByUuid_whenExists_shouldReturnCopy() throws EntityNotFoundException {
        Copy found = copyService.getCopyByUuid(existingCopy.getId());
        assertThat(found).isNotNull();
        assertThat(found.getCondition()).isEqualTo(CopyCondition.NEW);
    }

    @Test
    void getCopyByUuid_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> copyService.getCopyByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getCopiesPaginated_shouldReturnAllCopies() {
        Page<Copy> copies = copyService.getCopiesPaginated(PageRequest.of(0, 10));
        assertThat(copies.getContent()).hasSize(1);
    }

    @Test
    void updateCopy_whenSetAvailableFalse_shouldUpdateCopy()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        CopyUpdateDTO dto = new CopyUpdateDTO(false, CopyCondition.GOOD);
        Copy updated = copyService.updateCopy(existingCopy.getId(), dto);
        assertThat(updated.getAvailable()).isFalse();
        assertThat(updated.getCondition()).isEqualTo(CopyCondition.GOOD);
    }
}