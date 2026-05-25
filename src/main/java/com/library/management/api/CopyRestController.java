package com.library.management.api;

import com.library.management.core.exceptions.*;
import com.library.management.dto.*;
import com.library.management.mapper.CopyMapper;
import com.library.management.model.Copy;
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
    private final CopyMapper copyMapper;

    @PostMapping
    public ResponseEntity<CopyReadOnlyDTO> saveCopy(@Valid @RequestBody CopyInsertDTO dto)
            throws EntityInvalidArgumentException, EntityNotFoundException {
        Copy savedCopy = copyService.saveCopy(dto);
        CopyReadOnlyDTO responseDTO = copyMapper.mapToCopyReadOnlyDTO(savedCopy);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<CopyReadOnlyDTO> updateCopy(@PathVariable UUID uuid,
                                                      @Valid @RequestBody CopyUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Copy updatedCopy = copyService.updateCopy(uuid, dto);
        return ResponseEntity.ok(copyMapper.mapToCopyReadOnlyDTO(updatedCopy));
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
        Copy copy = copyService.getCopyByUuidDeletedFalse(uuid);
        return ResponseEntity.ok(copyMapper.mapToCopyReadOnlyDTO(copy));
    }

    @GetMapping
    public ResponseEntity<Page<CopyReadOnlyDTO>> getCopies(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<Copy> copies = copyService.getCopiesPaginatedAndDeletedFalse(pageable);
        return ResponseEntity.ok(copies.map(copyMapper::mapToCopyReadOnlyDTO));
    }
}