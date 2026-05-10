package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.CopyInsertDTO;
import com.library.management.dto.CopyReadOnlyDTO;
import com.library.management.dto.CopyUpdateDTO;
import com.library.management.mapper.CopyMapper;
import com.library.management.model.Book;
import com.library.management.model.Copy;
import com.library.management.model.Rental;
import com.library.management.repository.BookRepository;
import com.library.management.repository.CopyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CopyServiceImpl implements ICopyService {

    private final CopyRepository copyRepository;
    private final BookRepository bookRepository;
    private final CopyMapper mapper;

    @Override
    @Transactional(rollbackFor = {EntityInvalidArgumentException.class, EntityNotFoundException.class})
    public CopyReadOnlyDTO saveCopy(CopyInsertDTO dto) throws EntityInvalidArgumentException, EntityNotFoundException {

        try {
            Book book = bookRepository.findByUuid(dto.bookUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Book", "Book with uuid=" + dto.bookUuid() + " not found"));

            if (book.isDeleted()) {
                throw new EntityInvalidArgumentException("Copy", "Cannot add copy to a deleted book");
            }

            Copy copy = mapper.mapToCopyEntity(dto);

            copy.setBook(book);

            Copy savedCopy = copyRepository.save(copy);
            log.info("Copy saved with uuid={}", savedCopy.getId());

            return mapper.mapToCopyReadOnlyDTO(savedCopy);
        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.error("Save copy failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public CopyReadOnlyDTO updateCopy(CopyUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {

        try {

            Copy copy = copyRepository.findByUuid(dto.uuid())
                    .orElseThrow(() -> new EntityNotFoundException("Copy", "Copy with uuid=" + dto.uuid() + " not found"));

            if (dto.available() && copy.getAllRentals().stream().anyMatch(Rental::isActive)) {
                throw new EntityInvalidArgumentException("Copy",
                        "Cannot set copy as available while it has active rentals");
            }

            copy.setAvailable(dto.available());
            copy.setCondition(dto.condition());

            Copy updatedCopy = copyRepository.save(copy);
            log.info("Copy updated with uuid={}", updatedCopy.getId());
            return mapper.mapToCopyReadOnlyDTO(updatedCopy);

        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.error("Update copy failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public void deleteCopyByUuid(UUID uuid) throws EntityNotFoundException, EntityInvalidArgumentException {

        try {
            Copy copy = copyRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Copy", "Copy with uuid= " + uuid + " not found"));

            boolean hasActiveRental = copy.getAllRentals().stream()
                    .anyMatch(Rental::isActive);

            if (hasActiveRental) {
                throw new EntityInvalidArgumentException("Copy",
                        "Cannot delete copy with active rental");
            }

            copy.softDelete();
            copyRepository.save(copy);
            log.info("Copy with uuid={} deleted successfully", uuid);
        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.error("Delete copy by uuid failed.{}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CopyReadOnlyDTO getCopyByUuid(UUID uuid) throws EntityNotFoundException {

        try {
            Copy copy = copyRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Copy", "Copy with uuid= " + uuid + " not found."));

            log.info("Copy with uuid={} returned successfully.", uuid);

            return mapper.mapToCopyReadOnlyDTO(copy);
        } catch (EntityNotFoundException e) {
            log.error("Get copy by uuid failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CopyReadOnlyDTO getCopyByUuidDeletedFalse(UUID uuid) throws EntityNotFoundException {

        try {
            Copy copy = copyRepository.findByUuidAndDeletedFalse(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Copy", "Copy with uuid= " + uuid + " not found."));

            log.info("Copy with uuid={} and deleted false returned successfully.", uuid);

            return mapper.mapToCopyReadOnlyDTO(copy);
        } catch (EntityNotFoundException e) {
            log.error("Get copy by uuid failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CopyReadOnlyDTO> getCopiesPaginated(Pageable pageable) {

        Page<Copy> copyPage = copyRepository.findAll(pageable);
        log.info("Get paginated returned successfully page={} and size={}", copyPage.getNumber(), copyPage.getSize());
        return copyPage.map(mapper::mapToCopyReadOnlyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CopyReadOnlyDTO> getCopiesPaginatedAndDeletedFalse(Pageable pageable) {

        Page<Copy> copyPage = copyRepository.findByDeletedFalse(pageable);
        log.info("Get paginated not deleted returned successfully page={} and size={}", copyPage.getNumber(), copyPage.getSize());
        return copyPage.map(mapper::mapToCopyReadOnlyDTO);
    }

}
