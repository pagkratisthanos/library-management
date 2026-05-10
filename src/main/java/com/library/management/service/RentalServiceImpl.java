package com.library.management.service;

import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.RentalInsertDTO;
import com.library.management.dto.RentalReadOnlyDTO;
import com.library.management.mapper.RentalMapper;
import com.library.management.model.Copy;
import com.library.management.model.Member;
import com.library.management.model.Rental;
import com.library.management.repository.CopyRepository;
import com.library.management.repository.MemberRepository;
import com.library.management.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalServiceImpl implements IRentalService {
    private final RentalRepository rentalRepository;
    private final MemberRepository memberRepository;
    private final CopyRepository copyRepository;
    private final RentalMapper mapper;

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public RentalReadOnlyDTO saveRental(RentalInsertDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {

        try {
            Member member = memberRepository.findByUuidAndDeletedFalse(dto.memberUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Member", "Member with uuid=" + dto.memberUuid() + " not found"));

            Copy copy = copyRepository.findByUuidAndDeletedFalse(dto.copyUuid())
                    .orElseThrow(() -> new EntityNotFoundException("Copy", "Copy with uuid=" + dto.copyUuid() + " not found"));

            if (!copy.getAvailable()) {
                throw new EntityInvalidArgumentException("Rental", "Copy is not available for rental");
            }

            if (dto.dueDate().isBefore(Instant.now())) {
                throw new EntityInvalidArgumentException("Rental", "Due date cannot be in the past");
            }

            Rental rental = mapper.mapToRentalEntity(dto);
            rental.setMember(member);
            rental.setCopy(copy);
            rental.setRentalDate(Instant.now());

            copy.setAvailable(false);
            copyRepository.save(copy);

            Rental savedRental = rentalRepository.save(rental);
            log.info("Rental saved with uuid={}", savedRental.getId());
            return mapper.mapToRentalReadOnlyDTO(savedRental);

        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.error("Save rental failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {EntityNotFoundException.class, EntityInvalidArgumentException.class})
    public RentalReadOnlyDTO returnRental(UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {

        try {
            Rental rental = rentalRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Rental", "Rental with uuid=" + uuid + " not found"));

            if (!rental.isActive()) {
                throw new EntityInvalidArgumentException("Rental", "Rental has already been returned");
            }

            rental.setReturnDate(Instant.now());
            rental.getCopy().setAvailable(true);
            copyRepository.save(rental.getCopy());

            Rental returnedRental = rentalRepository.save(rental);
            log.info("Rental with uuid={} returned successfully", uuid);

            return mapper.mapToRentalReadOnlyDTO(returnedRental);
        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            log.error("Return rental failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RentalReadOnlyDTO getRentalByUuid(UUID uuid) throws EntityNotFoundException {

        try {
            Rental rental = rentalRepository.findByUuid(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Rental", "Rental with uuid=" + uuid + " not found"));

            return mapper.mapToRentalReadOnlyDTO(rental);
        } catch (EntityNotFoundException e) {
            log.error("Get rental by uuid failed. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalReadOnlyDTO> getRentalsByMemberUuid(UUID memberUuid) throws EntityNotFoundException {

        try {
            Member member = memberRepository.findByUuid(memberUuid)
                    .orElseThrow(() -> new EntityNotFoundException("Member", "Member with uuid= " + memberUuid + " not found."));

            List<Rental> rentals = rentalRepository.findByMember_Uuid(memberUuid);
            log.info("Get rentals by memberUuid={} returned successfully", memberUuid);

            return rentals.stream()
                    .map(mapper::mapToRentalReadOnlyDTO)
                    .collect(Collectors.toList());

        } catch (EntityNotFoundException e) {
            log.error("Get rentals by memberUuid={} failed. {}", memberUuid, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalReadOnlyDTO> getRentalsByCopyUuid(UUID copyUuid) throws EntityNotFoundException {

        try {
            Copy copy = copyRepository.findByUuid(copyUuid)
                    .orElseThrow(() -> new EntityNotFoundException("Copy", "Copy with uuid= " + copyUuid + " not found."));

            List<Rental> rentals = rentalRepository.findByCopy_Uuid(copyUuid);
            log.info("Get rentals by memberUuid={} returned successfully", copyUuid);

            return rentals.stream()
                    .map(mapper::mapToRentalReadOnlyDTO)
                    .collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            log.error("Get rentals by copyUuid={} failed. {}", copyUuid, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalReadOnlyDTO> getRentalsPaginated(Pageable pageable) {

        Page<Rental> rentalPage = rentalRepository.findAll(pageable);
        log.info("Get rentals paginated returned successfully page={} and size={}",
                rentalPage.getNumber(), rentalPage.getSize());
        return rentalPage.map(mapper::mapToRentalReadOnlyDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RentalReadOnlyDTO> getActiveRentalsPaginated(Pageable pageable) {

        Page<Rental> rentalPage = rentalRepository.findByReturnDateIsNull(pageable);
        log.info("Get active rentals paginated returned successfully page={} and size={}",
                rentalPage.getNumber(), rentalPage.getSize());
        return rentalPage.map(mapper::mapToRentalReadOnlyDTO);
    }

}
