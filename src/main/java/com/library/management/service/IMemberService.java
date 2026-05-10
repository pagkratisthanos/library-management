package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberReadOnlyDTO;
import com.library.management.dto.MemberUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IMemberService {

    MemberReadOnlyDTO saveMember(MemberInsertDTO memberInsertDTO)
        throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    MemberReadOnlyDTO updateMember(UUID id, MemberUpdateDTO memberUpdateDTO)
        throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidArgumentException;

    void deleteMemberByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    MemberReadOnlyDTO getMemberByUuid(UUID uuid) throws EntityNotFoundException;

    MemberReadOnlyDTO getMemberByUUIDDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<MemberReadOnlyDTO> getMembersPaginated(Pageable pageable);

    Page<MemberReadOnlyDTO> getMembersPaginatedAndDeletedFalse(Pageable pageable);

    boolean isMemberExistByEmail(String email);

    MemberReadOnlyDTO getMemberByEmail(String email) throws EntityNotFoundException;

    MemberReadOnlyDTO getMemberByPhoneNumber(String phoneNumber) throws EntityNotFoundException;

}
