package com.library.management.specification;

import com.library.management.core.filters.MemberFilters;
import com.library.management.model.Address;
import com.library.management.model.Member;
import com.library.management.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MemberSpecificationTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member thanos;
    private Member john;

    @BeforeEach
    void setUp() {
        Address address1 = new Address();
        address1.setStreet("Ermou");
        address1.setStreetNumber("10");
        address1.setCity("Athens");
        address1.setCountry("Greece");
        address1.setPostalCode("10563");

        thanos = new Member();
        thanos.setFirstname("Thanos");
        thanos.setLastname("Pagkratis");
        thanos.setEmail("thanos@example.com");
        thanos.setPhoneNumber("6912345678");
        thanos.setBirthDate(LocalDate.of(1990, 1, 1));
        thanos.setMembershipDate(LocalDate.of(2024, 1, 1));
        thanos.setAddress(address1);
        memberRepository.save(thanos);

        Address address2 = new Address();
        address2.setStreet("Stadiou");
        address2.setStreetNumber("5");
        address2.setCity("Athens");
        address2.setCountry("Greece");
        address2.setPostalCode("10564");

        john = new Member();
        john.setFirstname("John");
        john.setLastname("Doe");
        john.setEmail("john@example.com");
        john.setPhoneNumber("6900000001");
        john.setBirthDate(LocalDate.of(1985, 5, 15));
        john.setMembershipDate(LocalDate.of(2024, 1, 1));
        john.setAddress(address2);
        memberRepository.save(john);
    }

    @Test
    void build_whenNoFilters_shouldReturnAllActiveMembers() {
        MemberFilters filters = new MemberFilters();
        Page<Member> result = memberRepository.findAll(MemberSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void build_whenFilterByLastname_shouldReturnMatchingMembers() {
        MemberFilters filters = new MemberFilters();
        filters.setLastname("Pagkratis");
        Page<Member> result = memberRepository.findAll(MemberSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastname()).isEqualTo("Pagkratis");
    }

    @Test
    void build_whenFilterByFirstname_shouldReturnMatchingMembers() {
        MemberFilters filters = new MemberFilters();
        filters.setFirstname("John");
        Page<Member> result = memberRepository.findAll(MemberSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstname()).isEqualTo("John");
    }

    @Test
    void build_whenFilterByEmail_shouldReturnMatchingMembers() {
        MemberFilters filters = new MemberFilters();
        filters.setEmail("thanos");
        Page<Member> result = memberRepository.findAll(MemberSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("thanos@example.com");
    }

    @Test
    void build_whenFilterByPhoneNumber_shouldReturnMatchingMembers() {
        MemberFilters filters = new MemberFilters();
        filters.setPhoneNumber("6912345678");
        Page<Member> result = memberRepository.findAll(MemberSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPhoneNumber()).isEqualTo("6912345678");
    }

    @Test
    void build_whenDeletedMember_shouldNotReturn() {
        thanos.softDelete();
        memberRepository.save(thanos);

        MemberFilters filters = new MemberFilters();
        Page<Member> result = memberRepository.findAll(MemberSpecification.build(filters), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastname()).isEqualTo("Doe");
    }
}