package com.library.management.api;

import com.library.management.core.exceptions.*;
import com.library.management.dto.*;
import com.library.management.mapper.BookMapper;
import com.library.management.model.Book;
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
    private final BookMapper bookMapper;

    @PostMapping
    public ResponseEntity<BookReadOnlyDTO> saveBook(@Valid @RequestBody BookInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        Book savedBook = bookService.saveBook(dto);
        BookReadOnlyDTO responseDTO = bookMapper.mapToBookReadOnlyDTO(savedBook);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<BookReadOnlyDTO> updateBook(@PathVariable UUID uuid,
                                                      @Valid @RequestBody BookUpdateDTO dto)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        Book updatedBook = bookService.updateBook(uuid, dto);
        return ResponseEntity.ok(bookMapper.mapToBookReadOnlyDTO(updatedBook));
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
        Book book = bookService.getBookByUuidDeletedFalse(uuid);
        return ResponseEntity.ok(bookMapper.mapToBookReadOnlyDTO(book));
    }

    @GetMapping
    public ResponseEntity<Page<BookReadOnlyDTO>> getBooks(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<Book> books = bookService.getBooksPaginatedAndDeletedFalse(pageable);
        return ResponseEntity.ok(books.map(bookMapper::mapToBookReadOnlyDTO));
    }
}