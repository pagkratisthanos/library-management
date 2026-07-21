package com.library.management.mapper;

import com.library.management.dto.AddressInsertDTO;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberReadOnlyDTO;
import com.library.management.model.Address;
import com.library.management.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MemberMapperTest {

    private MemberMapper memberMapper;
    private Member member;
    private Address address;

    @BeforeEach
    void setUp() {
        memberMapper = new MemberMapper();

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
    }

    @Test
    void mapToMemberEntity_shouldMapCorrectly() {
        AddressInsertDTO addressDTO = new AddressInsertDTO(
                "Ermou", "10", "Athens", "Greece", "10563"
        );

        MemberInsertDTO dto = new MemberInsertDTO(
                addressDTO, "Thanos", "Pagkratis", "6912345678",
                "thanos@example.com", LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );

        Member mapped = memberMapper.mapToMemberEntity(dto);

        assertThat(mapped.getFirstname()).isEqualTo("Thanos");
        assertThat(mapped.getLastname()).isEqualTo("Pagkratis");
        assertThat(mapped.getEmail()).isEqualTo("thanos@example.com");
        assertThat(mapped.getPhoneNumber()).isEqualTo("6912345678");
        assertThat(mapped.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(mapped.getMembershipDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(mapped.getAddress().getCity()).isEqualTo("Athens");
    }

    @Test
    void mapToMemberReadOnlyDTO_shouldMapCorrectly() {
        MemberReadOnlyDTO dto = memberMapper.mapToMemberReadOnlyDTO(member);

        assertThat(dto.firstname()).isEqualTo("Thanos");
        assertThat(dto.lastname()).isEqualTo("Pagkratis");
        assertThat(dto.email()).isEqualTo("thanos@example.com");
        assertThat(dto.phoneNumber()).isEqualTo("6912345678");
        assertThat(dto.birthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(dto.membershipDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    void mapToMemberReadOnlyDTO_shouldMapAddress() {
        MemberReadOnlyDTO dto = memberMapper.mapToMemberReadOnlyDTO(member);

        assertThat(dto.addressReadOnlyDTO()).isNotNull();
        assertThat(dto.addressReadOnlyDTO().street()).isEqualTo("Ermou");
        assertThat(dto.addressReadOnlyDTO().city()).isEqualTo("Athens");
        assertThat(dto.addressReadOnlyDTO().country()).isEqualTo("Greece");
    }
}