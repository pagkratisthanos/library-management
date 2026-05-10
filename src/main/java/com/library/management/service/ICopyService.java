package com.library.management.service;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.CopyInsertDTO;
import com.library.management.dto.CopyReadOnlyDTO;
import com.library.management.dto.CopyUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICopyService {

    CopyReadOnlyDTO saveCopy(CopyInsertDTO dto) throws EntityInvalidArgumentException, EntityNotFoundException;

    CopyReadOnlyDTO updateCopy(UUID id, CopyUpdateDTO dto) throws EntityNotFoundException, EntityInvalidArgumentException;

    void deleteCopyByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    CopyReadOnlyDTO getCopyByUuid(UUID uuid) throws EntityNotFoundException;

    CopyReadOnlyDTO getCopyByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<CopyReadOnlyDTO> getCopiesPaginated(Pageable pageable);

    Page<CopyReadOnlyDTO> getCopiesPaginatedAndDeletedFalse(Pageable pageable);
}
