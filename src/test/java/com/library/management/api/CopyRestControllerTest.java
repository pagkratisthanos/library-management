package com.library.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.CopyInsertDTO;
import com.library.management.dto.CopyReadOnlyDTO;
import com.library.management.dto.CopyUpdateDTO;
import com.library.management.mapper.CopyMapper;
import com.library.management.model.Copy;
import com.library.management.model.CopyCondition;
import com.library.management.service.ICopyService;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class CopyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICopyService copyService;

    @MockitoBean
    private CopyMapper copyMapper;

    private Copy copy;
    private CopyReadOnlyDTO copyReadOnlyDTO;
    private UUID copyId;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        copyId = UUID.randomUUID();
        bookId = UUID.randomUUID();

        copy = new Copy();
        copy.setAvailable(true);
        copy.setCondition(CopyCondition.NEW);

        copyReadOnlyDTO = new CopyReadOnlyDTO(
                copyId, bookId, "Animal Farm", true, CopyCondition.NEW
        );
    }

    @Test
    void saveCopy_whenValidData_shouldReturn201() throws Exception {
        CopyInsertDTO dto = new CopyInsertDTO(bookId, true, CopyCondition.NEW);

        when(copyService.saveCopy(any())).thenReturn(copy);
        when(copyMapper.mapToCopyReadOnlyDTO(any())).thenReturn(copyReadOnlyDTO);

        mockMvc.perform(post("/api/copies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.condition").value("NEW"));
    }

    @Test
    void saveCopy_whenBookNotFound_shouldReturn404() throws Exception {
        CopyInsertDTO dto = new CopyInsertDTO(bookId, true, CopyCondition.NEW);

        when(copyService.saveCopy(any()))
                .thenThrow(new EntityNotFoundException("Book", "Not found"));

        mockMvc.perform(post("/api/copies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveCopy_whenBookDeleted_shouldReturn400() throws Exception {
        CopyInsertDTO dto = new CopyInsertDTO(bookId, true, CopyCondition.NEW);

        when(copyService.saveCopy(any()))
                .thenThrow(new EntityInvalidArgumentException("Copy", "Book is deleted"));

        mockMvc.perform(post("/api/copies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCopy_whenExists_shouldReturn200() throws Exception {
        CopyUpdateDTO dto = new CopyUpdateDTO(true, CopyCondition.GOOD);

        when(copyService.updateCopy(any(), any())).thenReturn(copy);
        when(copyMapper.mapToCopyReadOnlyDTO(any())).thenReturn(copyReadOnlyDTO);

        mockMvc.perform(put("/api/copies/{uuid}", copyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.condition").value("NEW"));
    }

    @Test
    void updateCopy_whenNotFound_shouldReturn404() throws Exception {
        CopyUpdateDTO dto = new CopyUpdateDTO(true, CopyCondition.GOOD);

        when(copyService.updateCopy(any(), any()))
                .thenThrow(new EntityNotFoundException("Copy", "Not found"));

        mockMvc.perform(put("/api/copies/{uuid}", copyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCopy_whenExists_shouldReturn204() throws Exception {
        doNothing().when(copyService).deleteCopyByUuid(any());

        mockMvc.perform(delete("/api/copies/{uuid}", copyId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCopy_whenNotFound_shouldReturn404() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Copy", "Not found"))
                .when(copyService).deleteCopyByUuid(any());

        mockMvc.perform(delete("/api/copies/{uuid}", copyId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCopy_whenHasActiveRental_shouldReturn400() throws Exception {
        org.mockito.Mockito.doThrow(new EntityInvalidArgumentException("Copy", "Has active rental"))
                .when(copyService).deleteCopyByUuid(any());

        mockMvc.perform(delete("/api/copies/{uuid}", copyId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCopy_whenExists_shouldReturn200() throws Exception {
        when(copyService.getCopyByUuidDeletedFalse(any())).thenReturn(copy);
        when(copyMapper.mapToCopyReadOnlyDTO(any())).thenReturn(copyReadOnlyDTO);

        mockMvc.perform(get("/api/copies/{uuid}", copyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.condition").value("NEW"));
    }

    @Test
    void getCopy_whenNotFound_shouldReturn404() throws Exception {
        when(copyService.getCopyByUuidDeletedFalse(any()))
                .thenThrow(new EntityNotFoundException("Copy", "Not found"));

        mockMvc.perform(get("/api/copies/{uuid}", copyId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCopies_shouldReturn200WithPage() throws Exception {
        Page<Copy> page = new PageImpl<>(List.of(copy), PageRequest.of(0, 10), 1);
        when(copyService.getCopiesPaginatedAndDeletedFalse(any())).thenReturn(page);
        when(copyMapper.mapToCopyReadOnlyDTO(any())).thenReturn(copyReadOnlyDTO);

        mockMvc.perform(get("/api/copies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].condition").value("NEW"));
    }
}