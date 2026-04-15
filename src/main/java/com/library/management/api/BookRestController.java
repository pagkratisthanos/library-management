package com.library.management.api;

import com.library.management.core.exceptions.*;
import com.library.management.dto.*;
import com.library.management.service.IBookService;
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
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final IBookService bookService;

    @PostMapping
    public ResponseEntity<BookReadOnlyDTO> saveBook(@Valid @RequestBody BookInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        BookReadOnlyDTO savedBook = bookService.saveBook(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(savedBook.uuid())
                .toUri();
        return ResponseEntity.created(location).body(savedBook);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<BookReadOnlyDTO> updateBook(@PathVariable UUID uuid,
                                                      @Valid @RequestBody BookUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        return ResponseEntity.ok(bookService.updateBook(dto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        bookService.deleteBookByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<BookReadOnlyDTO> getBook(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        return ResponseEntity.ok(bookService.getBookByUuidDeletedFalse(uuid));
    }

    @GetMapping
    public ResponseEntity<Page<BookReadOnlyDTO>> getBooks(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookService.getBooksPaginatedAndDeletedFalse(pageable));
    }
}
