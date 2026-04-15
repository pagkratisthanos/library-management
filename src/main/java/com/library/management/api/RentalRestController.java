package com.library.management.api;

import com.library.management.core.exceptions.*;
import com.library.management.dto.*;
import com.library.management.service.IRentalService;
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

    @PostMapping
    public ResponseEntity<RentalReadOnlyDTO> saveRental(@Valid @RequestBody RentalInsertDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        RentalReadOnlyDTO savedRental = rentalService.saveRental(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(savedRental.uuid())
                .toUri();
        return ResponseEntity.created(location).body(savedRental);
    }

    @PutMapping("/{uuid}/return")
    public ResponseEntity<RentalReadOnlyDTO> returnRental(@PathVariable UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        return ResponseEntity.ok(rentalService.returnRental(uuid));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<RentalReadOnlyDTO> getRental(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        return ResponseEntity.ok(rentalService.getRentalByUuid(uuid));
    }

    @GetMapping
    public ResponseEntity<Page<RentalReadOnlyDTO>> getRentals(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(rentalService.getRentalsPaginated(pageable));
    }

    @GetMapping("/member/{memberUuid}")
    public ResponseEntity<List<RentalReadOnlyDTO>> getRentalsByMember(@PathVariable UUID memberUuid)
            throws EntityNotFoundException {
        return ResponseEntity.ok(rentalService.getRentalsByMemberUuid(memberUuid));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<RentalReadOnlyDTO>> getActiveRentals(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(rentalService.getActiveRentalsPaginated(pageable));
    }
}