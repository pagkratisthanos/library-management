package com.library.management.api;

import com.library.management.core.exceptions.*;
import com.library.management.core.filters.RentalFilters;
import com.library.management.dto.*;
import com.library.management.mapper.RentalMapper;
import com.library.management.model.Rental;
import com.library.management.service.IRentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalRestController {

    private final IRentalService rentalService;
    private final RentalMapper rentalMapper;

    @Operation(summary = "Save a rental")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<RentalReadOnlyDTO> saveRental(@Valid @RequestBody RentalInsertDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Rental savedRental = rentalService.saveRental(dto);
        RentalReadOnlyDTO responseDTO = rentalMapper.mapToRentalReadOnlyDTO(savedRental);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @Operation(summary = "Return a rental")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{uuid}/return")
    public ResponseEntity<RentalReadOnlyDTO> returnRental(@PathVariable UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Rental returnedRental = rentalService.returnRental(uuid);
        return ResponseEntity.ok(rentalMapper.mapToRentalReadOnlyDTO(returnedRental));
    }

    @Operation(summary = "Extend a rental")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{uuid}/extend")
    public ResponseEntity<RentalReadOnlyDTO> extendRental(
            @PathVariable UUID uuid,
            @Valid @RequestBody RentalExtendDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Rental extendedRental = rentalService.extendRental(uuid, dto);
        return ResponseEntity.ok(rentalMapper.mapToRentalReadOnlyDTO(extendedRental));
    }

    @Operation(summary = "Get a rental by uuid")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{uuid}")
    public ResponseEntity<RentalReadOnlyDTO> getRental(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        Rental rental = rentalService.getRentalByUuid(uuid);
        return ResponseEntity.ok(rentalMapper.mapToRentalReadOnlyDTO(rental));
    }

    @Operation(summary = "Get all rentals paginated")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<Page<RentalReadOnlyDTO>> getRentals(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @ModelAttribute RentalFilters filters) {
        Page<Rental> rentals = rentalService.getRentalsPaginatedFiltered(pageable, filters);
        return ResponseEntity.ok(rentals.map(rentalMapper::mapToRentalReadOnlyDTO));
    }

    @Operation(summary = "Get rentals by member uuid")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/member/{memberUuid}")
    public ResponseEntity<List<RentalReadOnlyDTO>> getRentalsByMember(@PathVariable UUID memberUuid)
            throws EntityNotFoundException {
        List<Rental> rentals = rentalService.getRentalsByMemberUuid(memberUuid);
        return ResponseEntity.ok(rentals.stream()
                .map(rentalMapper::mapToRentalReadOnlyDTO)
                .toList());
    }

    @Operation(summary = "Get all active rentals paginated")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/active")
    public ResponseEntity<Page<RentalReadOnlyDTO>> getActiveRentals(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<Rental> rentals = rentalService.getActiveRentalsPaginated(pageable);
        return ResponseEntity.ok(rentals.map(rentalMapper::mapToRentalReadOnlyDTO));
    }

    @Operation(summary = "Get all overdue rentals paginated")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/overdue")
    public ResponseEntity<Page<RentalReadOnlyDTO>> getOverdueRentals(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<Rental> rentals = rentalService.getOverdueRentalsPaginated(pageable);
        return ResponseEntity.ok(rentals.map(rentalMapper::mapToRentalReadOnlyDTO));
    }
}