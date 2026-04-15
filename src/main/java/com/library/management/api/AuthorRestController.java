package com.library.management.api;

import com.library.management.core.exceptions.*;
import com.library.management.dto.*;
import com.library.management.service.IAuthorService;
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
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorRestController {

    private final IAuthorService authorService;

    @PostMapping
    public ResponseEntity<AuthorReadOnlyDTO> saveAuthor(@Valid @RequestBody AuthorInsertDTO dto)
            throws EntityInvalidArgumentException {
        AuthorReadOnlyDTO savedAuthor = authorService.saveAuthor(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(savedAuthor.uuid())
                .toUri();
        return ResponseEntity.created(location).body(savedAuthor);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<AuthorReadOnlyDTO> updateAuthor(@PathVariable UUID uuid,
                                                          @Valid @RequestBody AuthorUpdateDTO dto)
            throws EntityNotFoundException {
        return ResponseEntity.ok(authorService.updateAuthor(dto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        authorService.deleteAuthorByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<AuthorReadOnlyDTO> getAuthor(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        return ResponseEntity.ok(authorService.getAuthorByUUIDDeletedFalse(uuid));
    }

    @GetMapping
    public ResponseEntity<Page<AuthorReadOnlyDTO>> getAuthors(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(authorService.getAuthorsPaginatedAndDeletedFalse(pageable));
    }

    @GetMapping("/book/{bookUuid}")
    public ResponseEntity<List<AuthorReadOnlyDTO>> getAuthorsByBook(@PathVariable UUID bookUuid)
            throws EntityNotFoundException {
        return ResponseEntity.ok(authorService.getAuthorsByBookUuid(bookUuid));
    }
}