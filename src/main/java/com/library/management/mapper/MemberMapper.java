package com.library.management.mapper;

import com.library.management.dto.AddressReadOnlyDTO;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberReadOnlyDTO;
import com.library.management.model.Address;
import com.library.management.model.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member mapToMemberEntity(MemberInsertDTO insertDTO) {

        Address address = new Address();

        address.setCountry(insertDTO.addressInsertDTO().country());
        address.setCity(insertDTO.addressInsertDTO().city());
        address.setStreet(insertDTO.addressInsertDTO().street());
        address.setStreetNumber(insertDTO.addressInsertDTO().streetNumber());
        address.setPostalCode(insertDTO.addressInsertDTO().postalCode());

        Member member = new Member();

        member.setFirstname(insertDTO.firstname());
        member.setLastname(insertDTO.lastname());
        member.setBirthDate(insertDTO.birthDate());
        member.setEmail(insertDTO.email());
        member.setPhoneNumber(insertDTO.phoneNumber());
        member.setMembershipDate(insertDTO.membershipDate());
        member.setAddress(address);

        return member;
    }

    public MemberReadOnlyDTO mapToMemberReadOnlyDTO(Member member) {

        AddressReadOnlyDTO addressReadOnlyDTO = new AddressReadOnlyDTO(
                member.getAddress().getId(),
                member.getAddress().getStreet(),
                member.getAddress().getStreetNumber(),
                member.getAddress().getCity(),
                member.getAddress().getCountry(),
                member.getAddress().getPostalCode()
        );

        return new MemberReadOnlyDTO(
                member.getId(),
                addressReadOnlyDTO,
                member.getFirstname(),
                member.getLastname(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getMembershipDate(),
                member.getBirthDate()
        );
    }
}
