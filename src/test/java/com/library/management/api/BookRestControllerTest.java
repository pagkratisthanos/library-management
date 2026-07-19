package com.library.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.BookInsertDTO;
import com.library.management.dto.BookReadOnlyDTO;
import com.library.management.dto.BookUpdateDTO;
import com.library.management.mapper.BookMapper;
import com.library.management.model.Book;
import com.library.management.service.IBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IBookService bookService;

    @MockitoBean
    private BookMapper bookMapper;

    private Book book;
    private BookReadOnlyDTO bookReadOnlyDTO;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();

        book = new Book();
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-4");
        book.setLanguage("English");
        book.setDailyCost(BigDecimal.valueOf(1.20));

        bookReadOnlyDTO = new BookReadOnlyDTO(
                bookId, "Animal Farm", "978-0-452-28424-4",
                LocalDate.of(1945, 8, 17), "English",
                BigDecimal.valueOf(1.20), "A political allegory", Set.of()
        );
    }

    @Test
    void saveBook_whenValidData_shouldReturn201() throws Exception {
        BookInsertDTO dto = new BookInsertDTO(
                "Animal Farm", "978-0-452-28424-4",
                LocalDate.of(1945, 8, 17), "English",
                BigDecimal.valueOf(1.20), "A political allegory", null
        );

        when(bookService.saveBook(any())).thenReturn(book);
        when(bookMapper.mapToBookReadOnlyDTO(any())).thenReturn(bookReadOnlyDTO);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Animal Farm"))
                .andExpect(jsonPath("$.isbn").value("978-0-452-28424-4"));
    }

    @Test
    void saveBook_whenIsbnExists_shouldReturn409() throws Exception {
        BookInsertDTO dto = new BookInsertDTO(
                "Animal Farm", "978-0-452-28424-4",
                LocalDate.of(1945, 8, 17), "English",
                BigDecimal.valueOf(1.20), "A political allegory", null
        );

        when(bookService.saveBook(any()))
                .thenThrow(new EntityAlreadyExistsException("Book", "Already exists"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateBook_whenExists_shouldReturn200() throws Exception {
        BookUpdateDTO dto = new BookUpdateDTO("Greek", BigDecimal.valueOf(2.00), "Updated");

        when(bookService.updateBook(any(), any())).thenReturn(book);
        when(bookMapper.mapToBookReadOnlyDTO(any())).thenReturn(bookReadOnlyDTO);

        mockMvc.perform(put("/api/books/{uuid}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Animal Farm"));
    }

    @Test
    void updateBook_whenNotFound_shouldReturn404() throws Exception {
        BookUpdateDTO dto = new BookUpdateDTO("Greek", BigDecimal.valueOf(2.00), "Updated");

        when(bookService.updateBook(any(), any()))
                .thenThrow(new EntityNotFoundException("Book", "Not found"));

        mockMvc.perform(put("/api/books/{uuid}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_whenExists_shouldReturn204() throws Exception {
        doNothing().when(bookService).deleteBookByUuid(any());

        mockMvc.perform(delete("/api/books/{uuid}", bookId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_whenNotFound_shouldReturn404() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Book", "Not found"))
                .when(bookService).deleteBookByUuid(any());

        mockMvc.perform(delete("/api/books/{uuid}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_whenHasActiveRentals_shouldReturn400() throws Exception {
        org.mockito.Mockito.doThrow(new EntityInvalidArgumentException("Book", "Has active rentals"))
                .when(bookService).deleteBookByUuid(any());

        mockMvc.perform(delete("/api/books/{uuid}", bookId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBook_whenExists_shouldReturn200() throws Exception {
        when(bookService.getBookByUuidDeletedFalse(any())).thenReturn(book);
        when(bookMapper.mapToBookReadOnlyDTO(any())).thenReturn(bookReadOnlyDTO);

        mockMvc.perform(get("/api/books/{uuid}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Animal Farm"));
    }

    @Test
    void getBook_whenNotFound_shouldReturn404() throws Exception {
        when(bookService.getBookByUuidDeletedFalse(any()))
                .thenThrow(new EntityNotFoundException("Book", "Not found"));

        mockMvc.perform(get("/api/books/{uuid}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooks_shouldReturn200WithPage() throws Exception {
        Page<Book> page = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
        when(bookService.getBooksPaginatedAndDeletedFalse(any())).thenReturn(page);
        when(bookMapper.mapToBookReadOnlyDTO(any())).thenReturn(bookReadOnlyDTO);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Animal Farm"));
    }
}