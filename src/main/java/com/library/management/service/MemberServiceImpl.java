package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberUpdateDTO;
import com.library.management.model.Address;
import com.library.management.model.Member;
import com.library.management.model.Rental;
import com.library.management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements IMemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(rollbackFor = {EntityAlreadyExistsException.class, EntityInvalidArgumentException.class})
    public Member saveMember(MemberInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        try {
            if (dto.email() != null && memberRepository.existsByEmailAndDeletedFalse(dto.email())) {
                throw new EntityAlreadyExistsException("Member", "Member with email: " + dto.email() + " already exists");
            }

            if (dto.phoneNumber() != null && memberRepository.existsByPhoneNumberAndDeletedFalse(dto.phoneNumber())) {
                throw new EntityAlreadyExistsException("Member", "Member with phone number: " + dto.phoneNumber() + " already exists");
            }

            if (dto.birthDate() != null && dto.birthDate().isAfter(LocalDate.now())) {
                throw new EntityInvalidArgumentException("Member", "Birth date cannot be in the future");
            }

            if (dto.membershipDate().isAfter(LocalDate.now())) {
                throw new EntityInvalidArgumentException("Member", "Membership date cannot be in the future");
            }

            Address address = new Address();
            address.setCountry(dto.addressInsertDTO().country());
            address.setCity(dto.addressInsertDTO().city());
            address.setStreet(dto.addressInsertDTO().street());
            address.setStreetNumber(dto.addressInsertDTO().streetNumber());
            address.setPostalCode(dto.addressInsertDTO().postalCode());

            Member member = new Member();
            member.setFirstname(dto.firstname());
            member.setLastname(dto.lastname());
            member.setBirthDate(dto.birthDate());
            member.setEmail(dto.email());
            member.setPhoneNumber(dto.phoneNumber());
            member.setMembershipDate(dto.membershipDate());
            member.setAddress(address);

            Member savedMember = memberRepository.save(member);
            log.info("The member has successfully been saved.");
            return savedMember;

        } catch (EntityAlreadyExistsException e) {
            log.error("Save failed. Member already exists. {}", e.getMessage());
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.error("Save failed. Invalid argument for member. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityAlreadyExistsException.class,
            EntityInvalidArgumentException.class, EntityNotFoundException.class})
    public Member updateMember(UUID id, MemberUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException, EntityAlreadyExistsException {
        try {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Member", "Member with uuid: " + id + " not found"));

            if (memberRepository.existsByEmailAndIdNot(dto.email(), id)) {
                throw new EntityAlreadyExistsException("Member", "Member with email: " + dto.email() + " already exists");
            }

            if (memberRepository.existsByPhoneNumberAndIdNot(dto.phoneNumber(), id)) {
                throw new EntityAlreadyExistsException("Member", "Member with phone number: " + dto.phoneNumber() + " already exists");
            }

            if (dto.membershipDate() != null && dto.membershipDate().isAfter(LocalDate.now())) {
                throw new EntityInvalidArgumentException("Member", "Membership date cannot be in the future");
            }

            member.setMembershipDate(dto.membershipDate());
            member.setEmail(dto.email());
            member.setPhoneNumber(dto.phoneNumber());
            member.setLastname(dto.lastname());
            member.setFirstname(dto.firstname());

            Member updatedMember = memberRepository.save(member);
            log.info("Member updated with uuid={}.", updatedMember.getId());
            return updatedMember;

        } catch (EntityNotFoundException e) {
            log.error("Member not found. {}", e.getMessage());
            throw e;
        } catch (EntityAlreadyExistsException e) {
            log.error("Member already exists. {}", e.getMessage());
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.error("Invalid argument. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public void deleteMemberByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException {
        try {
            Member member = memberRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Member", "Member with uuid=" + uuid + " not found"));

            boolean hasActiveRentals = member.getAllRentals().stream()
                    .anyMatch(Rental::isActive);

            if (hasActiveRentals) {
                throw new EntityInvalidArgumentException("Member", "Cannot delete member with active rentals");
            }

            member.softDelete();
            member.getAddress().softDelete();
            memberRepository.save(member);
            log.info("Member with uuid={} deleted successfully", uuid);

        } catch (EntityNotFoundException e) {
            log.error("Delete failed for member with uuid={}. Member not found", uuid);
            throw e;
        } catch (EntityInvalidArgumentException e) {
            log.error("Delete failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberByUuid(UUID uuid) throws EntityNotFoundException {
        try {
            Member member = memberRepository.findById(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Member", "Member with uuid=" + uuid + " not found"));
            log.info("Get member by uuid={} returned successfully", uuid);
            return member;
        } catch (EntityNotFoundException e) {
            log.error("Get member by uuid={} failed", uuid);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException {
        try {
            Member member = memberRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Member", "Member with uuid=" + uuid + " not found"));
            log.info("Get non-deleted member by uuid={} returned successfully", uuid);
            return member;
        } catch (EntityNotFoundException e) {
            log.error("Get member by uuid={} failed", uuid);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Member> getMembersPaginated(Pageable pageable) {
        Page<Member> memberPage = memberRepository.findAll(pageable);
        log.info("Get paginated returned successfully page={} and size={}", memberPage.getNumber(), memberPage.getSize());
        return memberPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Member> getMembersPaginatedAndDeletedFalse(Pageable pageable) {
        Page<Member> memberPage = memberRepository.findByDeletedFalse(pageable);
        log.info("Get paginated not deleted returned successfully page={} and size={}", memberPage.getNumber(), memberPage.getSize());
        return memberPage;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMemberExistByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) throws EntityNotFoundException {
        try {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Member", "Member with email=" + email + " not found"));
            log.info("Member with email={} returned successfully", email);
            return member;
        } catch (EntityNotFoundException e) {
            log.error("Get member by email={} failed. {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Member getMemberByPhoneNumber(String phoneNumber) throws EntityNotFoundException {
        try {
            Member member = memberRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Member", "Member with phone=" + phoneNumber + " not found"));
            log.info("Member with phone={} returned successfully", phoneNumber);
            return member;
        } catch (EntityNotFoundException e) {
            log.error("Get member by phone={} failed. {}", phoneNumber, e.getMessage());
            throw e;
        }
    }
}