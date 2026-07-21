package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.AddressInsertDTO;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberUpdateDTO;
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
class MemberServiceTest {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private BookRepository bookRepository;

    private Member existingMember;

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
    }

    private AddressInsertDTO createAddressDTO() {
        return new AddressInsertDTO("Ermou", "10", "Athens", "Greece", "10563");
    }

    @Test
    void saveMember_whenValidData_shouldSaveAndReturnMember()
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        MemberInsertDTO dto = new MemberInsertDTO(
                createAddressDTO(), "John", "Doe", "6900000001",
                "john@example.com", LocalDate.of(1985, 5, 15), LocalDate.of(2024, 1, 1)
        );

        Member saved = memberService.saveMember(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void saveMember_whenEmailAlreadyExists_shouldThrowException() {
        MemberInsertDTO dto = new MemberInsertDTO(
                createAddressDTO(), "John", "Doe", "6900000001",
                "thanos@example.com", LocalDate.of(1985, 5, 15), LocalDate.of(2024, 1, 1)
        );

        assertThatThrownBy(() -> memberService.saveMember(dto))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    void saveMember_whenPhoneNumberAlreadyExists_shouldThrowException() {
        MemberInsertDTO dto = new MemberInsertDTO(
                createAddressDTO(), "John", "Doe", "6912345678",
                "john@example.com", LocalDate.of(1985, 5, 15), LocalDate.of(2024, 1, 1)
        );

        assertThatThrownBy(() -> memberService.saveMember(dto))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    void saveMember_whenBirthDateInFuture_shouldThrowException() {
        MemberInsertDTO dto = new MemberInsertDTO(
                createAddressDTO(), "John", "Doe", "6900000001",
                "john@example.com", LocalDate.now().plusDays(1), LocalDate.of(2024, 1, 1)
        );

        assertThatThrownBy(() -> memberService.saveMember(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void saveMember_whenMembershipDateInFuture_shouldThrowException() {
        MemberInsertDTO dto = new MemberInsertDTO(
                createAddressDTO(), "John", "Doe", "6900000001",
                "john@example.com", LocalDate.of(1985, 5, 15), LocalDate.now().plusDays(1)
        );

        assertThatThrownBy(() -> memberService.saveMember(dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void updateMember_whenValidData_shouldUpdateAndReturn()
            throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidArgumentException {
        MemberUpdateDTO dto = new MemberUpdateDTO(
                createAddressDTO(), "Updated", "Pagkratis", "6912345678",
                "thanos@example.com", LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );

        Member updated = memberService.updateMember(existingMember.getId(), dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getFirstname()).isEqualTo("Updated");
    }

    @Test
    void updateMember_whenNotFound_shouldThrowException() {
        MemberUpdateDTO dto = new MemberUpdateDTO(
                createAddressDTO(), "Updated", "Pagkratis", "6912345678",
                "thanos@example.com", LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );

        assertThatThrownBy(() -> memberService.updateMember(UUID.randomUUID(), dto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateMember_whenEmailAlreadyExistsForOtherMember_shouldThrowException() {
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
        anotherMember.setPhoneNumber("6900000002");
        anotherMember.setBirthDate(LocalDate.of(1990, 1, 1));
        anotherMember.setMembershipDate(LocalDate.of(2024, 1, 1));
        anotherMember.setAddress(address);
        memberRepository.save(anotherMember);

        MemberUpdateDTO dto = new MemberUpdateDTO(
                createAddressDTO(), "Thanos", "Pagkratis", "6912345678",
                "another@example.com", LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );

        assertThatThrownBy(() -> memberService.updateMember(existingMember.getId(), dto))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    void deleteMemberByUuid_whenNoActiveRentals_shouldSoftDelete()
            throws EntityNotFoundException, EntityInvalidArgumentException {
        memberService.deleteMemberByUuid(existingMember.getId());

        Member deleted = memberRepository.findById(existingMember.getId()).orElseThrow();
        assertThat(deleted.isDeleted()).isTrue();
        assertThat(deleted.getDeletedAt()).isNotNull();
    }

    @Test
    void deleteMemberByUuid_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> memberService.deleteMemberByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteMemberByUuid_whenHasActiveRentals_shouldThrowException() {
        Book book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));
        bookRepository.save(book);

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setAvailable(false);
        copy.setCondition(CopyCondition.NEW);
        copyRepository.save(copy);

        Rental rental = new Rental();
        rental.setMember(existingMember);
        rental.setCopy(copy);
        rental.setRentalDate(Instant.now());
        rental.setDueDate(Instant.now().plusSeconds(86400));
        rentalRepository.save(rental);
        existingMember.addRental(rental);
        memberRepository.save(existingMember);

        assertThatThrownBy(() -> memberService.deleteMemberByUuid(existingMember.getId()))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }

    @Test
    void getMemberByUUIDDeletedFalse_whenExists_shouldReturnMember() throws EntityNotFoundException {
        Member found = memberService.getMemberByUUIDDeletedFalse(existingMember.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void getMemberByUUIDDeletedFalse_whenDeleted_shouldThrowException() {
        existingMember.softDelete();
        memberRepository.save(existingMember);

        assertThatThrownBy(() -> memberService.getMemberByUUIDDeletedFalse(existingMember.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getMemberByUUIDDeletedFalse_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> memberService.getMemberByUUIDDeletedFalse(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getMembersPaginatedAndDeletedFalse_shouldReturnOnlyActiveMembers() {
        existingMember.softDelete();
        memberRepository.save(existingMember);

        Address address = new Address();
        address.setStreet("Stadiou");
        address.setStreetNumber("5");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10564");

        Member activeMember = new Member();
        activeMember.setFirstname("Active");
        activeMember.setLastname("Member");
        activeMember.setEmail("active@example.com");
        activeMember.setPhoneNumber("6900000001");
        activeMember.setBirthDate(LocalDate.of(1990, 1, 1));
        activeMember.setMembershipDate(LocalDate.of(2024, 1, 1));
        activeMember.setAddress(address);
        memberRepository.save(activeMember);

        Page<Member> members = memberService.getMembersPaginatedAndDeletedFalse(PageRequest.of(0, 10));
        assertThat(members.getContent()).hasSize(1);
        assertThat(members.getContent().get(0).getEmail()).isEqualTo("active@example.com");
    }

    @Test
    void isMemberExistByEmail_whenExists_shouldReturnTrue() {
        boolean exists = memberService.isMemberExistByEmail("thanos@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void isMemberExistByEmail_whenNotExists_shouldReturnFalse() {
        boolean exists = memberService.isMemberExistByEmail("notexist@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    void getMemberByEmail_whenExists_shouldReturnMember() throws EntityNotFoundException {
        Member found = memberService.getMemberByEmail("thanos@example.com");
        assertThat(found).isNotNull();
        assertThat(found.getFirstname()).isEqualTo("Thanos");
    }

    @Test
    void getMemberByEmail_whenNotExists_shouldThrowException() {
        assertThatThrownBy(() -> memberService.getMemberByEmail("notexist@example.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getMemberByPhoneNumber_whenExists_shouldReturnMember() throws EntityNotFoundException {
        Member found = memberService.getMemberByPhoneNumber("6912345678");
        assertThat(found).isNotNull();
        assertThat(found.getFirstname()).isEqualTo("Thanos");
    }

    @Test
    void getMemberByPhoneNumber_whenNotExists_shouldThrowException() {
        assertThatThrownBy(() -> memberService.getMemberByPhoneNumber("0000000000"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getMemberByUuid_whenExists_shouldReturnMember() throws EntityNotFoundException {
        Member found = memberService.getMemberByUuid(existingMember.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void getMemberByUuid_whenNotFound_shouldThrowException() {
        assertThatThrownBy(() -> memberService.getMemberByUuid(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getMembersPaginated_shouldReturnAllMembers() {
        Page<Member> members = memberService.getMembersPaginated(PageRequest.of(0, 10));
        assertThat(members.getContent()).hasSize(1);
    }

    @Test
    void updateMember_whenMembershipDateInFuture_shouldThrowException() {
        MemberUpdateDTO dto = new MemberUpdateDTO(
                createAddressDTO(), "Thanos", "Pagkratis", "6912345678",
                "thanos@example.com", LocalDate.of(1990, 1, 1), LocalDate.now().plusDays(1)
        );

        assertThatThrownBy(() -> memberService.updateMember(existingMember.getId(), dto))
                .isInstanceOf(EntityInvalidArgumentException.class);
    }
}