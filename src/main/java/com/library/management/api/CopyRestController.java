package com.library.management.api;

import com.library.management.core.exceptions.*;
import com.library.management.dto.*;
import com.library.management.service.ICopyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/copies")
@RequiredArgsConstructor
public class CopyRestController {

    private final ICopyService copyService;

    @PostMapping
    public ResponseEntity<CopyReadOnlyDTO> saveCopy(@Valid @RequestBody CopyInsertDTO dto)
            throws EntityInvalidArgumentException, EntityNotFoundException {
        CopyReadOnlyDTO savedCopy = copyService.saveCopy(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(savedCopy.uuid())
                .toUri();
        return ResponseEntity.created(location).body(savedCopy);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<CopyReadOnlyDTO> updateCopy(@PathVariable UUID uuid,
                                                      @Valid @RequestBody CopyUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        return ResponseEntity.ok(copyService.updateCopy(dto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteCopy(@PathVariable UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        copyService.deleteCopyByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CopyReadOnlyDTO> getCopy(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        return ResponseEntity.ok(copyService.getCopyByUuidDeletedFalse(uuid));
    }

    @GetMapping
    public ResponseEntity<Page<CopyReadOnlyDTO>> getCopies(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(copyService.getCopiesPaginatedAndDeletedFalse(pageable));
    }
}