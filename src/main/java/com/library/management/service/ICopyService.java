package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.CopyInsertDTO;
import com.library.management.dto.CopyUpdateDTO;
import com.library.management.model.Copy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICopyService {

    Copy saveCopy(CopyInsertDTO dto) throws EntityInvalidArgumentException, EntityNotFoundException;

    Copy updateCopy(UUID id, CopyUpdateDTO dto) throws EntityNotFoundException, EntityInvalidArgumentException;

    void deleteCopyByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException;

    Copy getCopyByUuid(UUID uuid) throws EntityNotFoundException;

    Copy getCopyByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException;

    Page<Copy> getCopiesPaginated(Pageable pageable);

    Page<Copy> getCopiesPaginatedAndDeletedFalse(Pageable pageable);
}
