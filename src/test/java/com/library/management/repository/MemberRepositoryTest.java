package com.library.management.repository;

import com.library.management.model.Address;
import com.library.management.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AddressRepository addressRepository;

    private Member member;

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
    }

    @Test
    void findByIdAndDeletedFalse_whenMemberExists_shouldReturnMember() {
        Optional<Member> found = memberRepository.findByIdAndDeletedFalse(member.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void findByIdAndDeletedFalse_whenMemberDeleted_shouldReturnEmpty() {
        member.softDelete();
        memberRepository.save(member);

        Optional<Member> found = memberRepository.findByIdAndDeletedFalse(member.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void findByDeletedFalse_shouldReturnOnlyActiveMembers() {
        Member deletedMember = new Member();
        Address address = new Address();
        address.setStreet("Stadiou");
        address.setStreetNumber("5");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10564");

        deletedMember.setFirstname("Deleted");
        deletedMember.setLastname("Member");
        deletedMember.setEmail("deleted@example.com");
        deletedMember.setPhoneNumber("6900000000");
        deletedMember.setBirthDate(LocalDate.of(1990, 1, 1));
        deletedMember.setMembershipDate(LocalDate.of(2024, 1, 1));
        deletedMember.setAddress(address);
        deletedMember.softDelete();
        memberRepository.save(deletedMember);

        Page<Member> members = memberRepository.findByDeletedFalse(PageRequest.of(0, 10));
        assertThat(members.getContent()).hasSize(1);
        assertThat(members.getContent().get(0).getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void findByEmail_whenExists_shouldReturnMember() {
        Optional<Member> found = memberRepository.findByEmail("thanos@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstname()).isEqualTo("Thanos");
    }

    @Test
    void findByEmail_whenNotExists_shouldReturnEmpty() {
        Optional<Member> found = memberRepository.findByEmail("notexist@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void findByPhoneNumber_whenExists_shouldReturnMember() {
        Optional<Member> found = memberRepository.findByPhoneNumber("6912345678");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstname()).isEqualTo("Thanos");
    }

    @Test
    void findByPhoneNumber_whenNotExists_shouldReturnEmpty() {
        Optional<Member> found = memberRepository.findByPhoneNumber("0000000000");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_whenExists_shouldReturnTrue() {
        boolean exists = memberRepository.existsByEmail("thanos@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_whenNotExists_shouldReturnFalse() {
        boolean exists = memberRepository.existsByEmail("notexist@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByPhoneNumber_whenExists_shouldReturnTrue() {
        boolean exists = memberRepository.existsByPhoneNumber("6912345678");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByPhoneNumber_whenNotExists_shouldReturnFalse() {
        boolean exists = memberRepository.existsByPhoneNumber("0000000000");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmailAndDeletedFalse_whenExistsAndNotDeleted_shouldReturnTrue() {
        boolean exists = memberRepository.existsByEmailAndDeletedFalse("thanos@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmailAndDeletedFalse_whenDeleted_shouldReturnFalse() {
        member.softDelete();
        memberRepository.save(member);

        boolean exists = memberRepository.existsByEmailAndDeletedFalse("thanos@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByPhoneNumberAndDeletedFalse_whenExistsAndNotDeleted_shouldReturnTrue() {
        boolean exists = memberRepository.existsByPhoneNumberAndDeletedFalse("6912345678");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByPhoneNumberAndDeletedFalse_whenDeleted_shouldReturnFalse() {
        member.softDelete();
        memberRepository.save(member);

        boolean exists = memberRepository.existsByPhoneNumberAndDeletedFalse("6912345678");
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmailAndIdNot_whenSameEmailDifferentId_shouldReturnTrue() {
        Member anotherMember = new Member();
        Address address = new Address();
        address.setStreet("Stadiou");
        address.setStreetNumber("5");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10564");

        anotherMember.setFirstname("Another");
        anotherMember.setLastname("Member");
        anotherMember.setEmail("another@example.com");
        anotherMember.setPhoneNumber("6900000001");
        anotherMember.setBirthDate(LocalDate.of(1990, 1, 1));
        anotherMember.setMembershipDate(LocalDate.of(2024, 1, 1));
        anotherMember.setAddress(address);
        memberRepository.save(anotherMember);

        boolean exists = memberRepository.existsByEmailAndIdNot("another@example.com", member.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmailAndIdNot_whenSameMemberId_shouldReturnFalse() {
        boolean exists = memberRepository.existsByEmailAndIdNot("thanos@example.com", member.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void existsByPhoneNumberAndIdNot_whenSamePhoneDifferentId_shouldReturnTrue() {
        Member anotherMember = new Member();
        Address address = new Address();
        address.setStreet("Stadiou");
        address.setStreetNumber("5");
        address.setCity("Athens");
        address.setCountry("Greece");
        address.setPostalCode("10564");

        anotherMember.setFirstname("Another");
        anotherMember.setLastname("Member");
        anotherMember.setEmail("another2@example.com");
        anotherMember.setPhoneNumber("6900000002");
        anotherMember.setBirthDate(LocalDate.of(1990, 1, 1));
        anotherMember.setMembershipDate(LocalDate.of(2024, 1, 1));
        anotherMember.setAddress(address);
        memberRepository.save(anotherMember);

        boolean exists = memberRepository.existsByPhoneNumberAndIdNot("6900000002", member.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByPhoneNumberAndIdNot_whenSameMemberId_shouldReturnFalse() {
        boolean exists = memberRepository.existsByPhoneNumberAndIdNot("6912345678", member.getId());
        assertThat(exists).isFalse();
    }
}