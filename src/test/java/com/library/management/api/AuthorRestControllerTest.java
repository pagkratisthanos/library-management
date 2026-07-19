package com.library.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.AuthorInsertDTO;
import com.library.management.dto.AuthorReadOnlyDTO;
import com.library.management.dto.AuthorUpdateDTO;
import com.library.management.mapper.AuthorMapper;
import com.library.management.model.Author;
import com.library.management.service.IAuthorService;
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
class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAuthorService authorService;

    @MockitoBean
    private AuthorMapper authorMapper;

    private Author author;
    private AuthorReadOnlyDTO authorReadOnlyDTO;
    private UUID authorId;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();

        author = new Author();
        author.setFirstname("George");
        author.setLastname("Orwell");
        author.setBirthDate(LocalDate.of(1903, 6, 25));

        authorReadOnlyDTO = new AuthorReadOnlyDTO(
                authorId, "George", "Orwell",
                LocalDate.of(1903, 6, 25), null, null, Set.of()
        );
    }

    @Test
    void saveAuthor_whenValidData_shouldReturn201() throws Exception {
        AuthorInsertDTO dto = new AuthorInsertDTO(
                "George", "Orwell", LocalDate.of(1903, 6, 25), null, null
        );

        when(authorService.saveAuthor(any())).thenReturn(author);
        when(authorMapper.mapToAuthorReadOnlyDTO(any())).thenReturn(authorReadOnlyDTO);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("George"))
                .andExpect(jsonPath("$.lastname").value("Orwell"));
    }

    @Test
    void saveAuthor_whenInvalidData_shouldReturn400() throws Exception {
        AuthorInsertDTO dto = new AuthorInsertDTO(
                "", "", null, null, null
        );

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAuthor_whenExists_shouldReturn200() throws Exception {
        when(authorService.updateAuthor(any(), any())).thenReturn(author);
        when(authorMapper.mapToAuthorReadOnlyDTO(any())).thenReturn(authorReadOnlyDTO);

        mockMvc.perform(put("/api/authors/{uuid}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AuthorUpdateDTO(
                                        "George", "Orwell",
                                        LocalDate.of(1903, 6, 25), null, null
                                )))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("George"));
    }

    @Test
    void updateAuthor_whenNotFound_shouldReturn404() throws Exception {
        when(authorService.updateAuthor(any(), any()))
                .thenThrow(new EntityNotFoundException("Author", "Not found"));

        mockMvc.perform(put("/api/authors/{uuid}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AuthorUpdateDTO(
                                        "George", "Orwell",
                                        LocalDate.of(1903, 6, 25), null, null
                                )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAuthor_whenExists_shouldReturn204() throws Exception {
        doNothing().when(authorService).deleteAuthorByUuid(any());

        mockMvc.perform(delete("/api/authors/{uuid}", authorId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAuthor_whenNotFound_shouldReturn404() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Author", "Not found"))
                .when(authorService).deleteAuthorByUuid(any());

        mockMvc.perform(delete("/api/authors/{uuid}", authorId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAuthor_whenHasBooks_shouldReturn400() throws Exception {
        org.mockito.Mockito.doThrow(new EntityInvalidArgumentException("Author", "Has books"))
                .when(authorService).deleteAuthorByUuid(any());

        mockMvc.perform(delete("/api/authors/{uuid}", authorId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAuthor_whenExists_shouldReturn200() throws Exception {
        when(authorService.getAuthorByUUIDDeletedFalse(any())).thenReturn(author);
        when(authorMapper.mapToAuthorReadOnlyDTO(any())).thenReturn(authorReadOnlyDTO);

        mockMvc.perform(get("/api/authors/{uuid}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("George"))
                .andExpect(jsonPath("$.lastname").value("Orwell"));
    }

    @Test
    void getAuthor_whenNotFound_shouldReturn404() throws Exception {
        when(authorService.getAuthorByUUIDDeletedFalse(any()))
                .thenThrow(new EntityNotFoundException("Author", "Not found"));

        mockMvc.perform(get("/api/authors/{uuid}", authorId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAuthors_shouldReturn200WithPage() throws Exception {
        Page<Author> page = new PageImpl<>(List.of(author), PageRequest.of(0, 10), 1);
        when(authorService.getAuthorsPaginatedAndDeletedFalse(any())).thenReturn(page);
        when(authorMapper.mapToAuthorReadOnlyDTO(any())).thenReturn(authorReadOnlyDTO);

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstname").value("George"));
    }

    @Test
    void getAuthorsByBook_whenBookExists_shouldReturn200() throws Exception {
        when(authorService.getAuthorsByBookUuid(any())).thenReturn(List.of(author));
        when(authorMapper.mapToAuthorReadOnlyDTO(any())).thenReturn(authorReadOnlyDTO);

        mockMvc.perform(get("/api/authors/book/{bookUuid}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstname").value("George"));
    }

    @Test
    void getAuthorsByBook_whenBookNotFound_shouldReturn404() throws Exception {
        when(authorService.getAuthorsByBookUuid(any()))
                .thenThrow(new EntityNotFoundException("Book", "Not found"));

        mockMvc.perform(get("/api/authors/book/{bookUuid}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}