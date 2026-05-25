package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberUpdateDTO;
import com.library.management.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IMemberService {

    Member saveMember(MemberInsertDTO dto) throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    Member updateMember(UUID id, MemberUpdateDTO dto) throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidArgumentException;

    void deleteMemberByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    Member getMemberByUuid(UUID uuid) throws EntityNotFoundException;

    Member getMemberByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<Member> getMembersPaginated(Pageable pageable);

    Page<Member> getMembersPaginatedAndDeletedFalse(Pageable pageable);

    boolean isMemberExistByEmail(String email);

    Member getMemberByEmail(String email) throws EntityNotFoundException;

    Member getMemberByPhoneNumber(String phoneNumber) throws EntityNotFoundException;

}
