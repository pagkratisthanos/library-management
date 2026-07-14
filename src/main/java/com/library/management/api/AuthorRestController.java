package com.library.management.api;

import com.library.management.core.exceptions.*;
import com.library.management.dto.*;
import com.library.management.mapper.AuthorMapper;
import com.library.management.model.Author;
import com.library.management.service.IAuthorService;
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
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorRestController {

    private final IAuthorService authorService;
    private final AuthorMapper authorMapper;

    @Operation(summary = "Save an author")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<AuthorReadOnlyDTO> saveAuthor(@Valid @RequestBody AuthorInsertDTO dto)
            throws EntityInvalidArgumentException {
        Author savedAuthor = authorService.saveAuthor(dto);
        AuthorReadOnlyDTO responseDTO = authorMapper.mapToAuthorReadOnlyDTO(savedAuthor);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @Operation(summary = "Update an author")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{uuid}")
    public ResponseEntity<AuthorReadOnlyDTO> updateAuthor(@PathVariable UUID uuid,
                                                          @Valid @RequestBody AuthorUpdateDTO dto)
            throws EntityNotFoundException {
        Author updatedAuthor = authorService.updateAuthor(uuid, dto);
        return ResponseEntity.ok(authorMapper.mapToAuthorReadOnlyDTO(updatedAuthor));
    }

    @Operation(summary = "Delete an author")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        authorService.deleteAuthorByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get an author by uuid")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{uuid}")
    public ResponseEntity<AuthorReadOnlyDTO> getAuthor(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        Author author = authorService.getAuthorByUUIDDeletedFalse(uuid);
        return ResponseEntity.ok(authorMapper.mapToAuthorReadOnlyDTO(author));
    }

    @Operation(summary = "Get all authors paginated")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<Page<AuthorReadOnlyDTO>> getAuthors(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<Author> authors = authorService.getAuthorsPaginatedAndDeletedFalse(pageable);
        return ResponseEntity.ok(authors.map(authorMapper::mapToAuthorReadOnlyDTO));
    }

    @Operation(summary = "Get authors by book uuid")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/book/{bookUuid}")
    public ResponseEntity<List<AuthorReadOnlyDTO>> getAuthorsByBook(@PathVariable UUID bookUuid)
            throws EntityNotFoundException {
        List<Author> authors = authorService.getAuthorsByBookUuid(bookUuid);
        return ResponseEntity.ok(authors.stream()
                .map(authorMapper::mapToAuthorReadOnlyDTO)
                .toList());
    }
}