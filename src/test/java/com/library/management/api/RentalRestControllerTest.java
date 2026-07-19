package com.library.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.RentalInsertDTO;
import com.library.management.dto.RentalReadOnlyDTO;
import com.library.management.mapper.RentalMapper;
import com.library.management.model.Rental;
import com.library.management.service.IRentalService;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RentalRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRentalService rentalService;

    @MockitoBean
    private RentalMapper rentalMapper;

    private Rental rental;
    private RentalReadOnlyDTO rentalReadOnlyDTO;
    private UUID rentalId;
    private UUID memberId;
    private UUID copyId;

    @BeforeEach
    void setUp() {
        rentalId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        copyId = UUID.randomUUID();

        rental = new Rental();
        rental.setRentalDate(Instant.now());
        rental.setDueDate(Instant.now().plusSeconds(86400 * 7));

        rentalReadOnlyDTO = new RentalReadOnlyDTO(
                rentalId, memberId, copyId,
                Instant.now(), Instant.now().plusSeconds(86400 * 7),
                null, "Thanos", "Pagkratis", "Animal Farm"
        );
    }

    @Test
    void saveRental_whenValidData_shouldReturn201() throws Exception {
        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().plusSeconds(86400 * 7), memberId, copyId
        );

        when(rentalService.saveRental(any())).thenReturn(rental);
        when(rentalMapper.mapToRentalReadOnlyDTO(any())).thenReturn(rentalReadOnlyDTO);

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberFirstname").value("Thanos"))
                .andExpect(jsonPath("$.bookTitle").value("Animal Farm"));
    }

    @Test
    void saveRental_whenCopyNotAvailable_shouldReturn400() throws Exception {
        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().plusSeconds(86400 * 7), memberId, copyId
        );

        when(rentalService.saveRental(any()))
                .thenThrow(new EntityInvalidArgumentException("Rental", "Copy not available"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveRental_whenMemberNotFound_shouldReturn404() throws Exception {
        RentalInsertDTO dto = new RentalInsertDTO(
                Instant.now().plusSeconds(86400 * 7), memberId, copyId
        );

        when(rentalService.saveRental(any()))
                .thenThrow(new EntityNotFoundException("Member", "Not found"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnRental_whenActive_shouldReturn200() throws Exception {
        when(rentalService.returnRental(any())).thenReturn(rental);
        when(rentalMapper.mapToRentalReadOnlyDTO(any())).thenReturn(rentalReadOnlyDTO);

        mockMvc.perform(put("/api/rentals/{uuid}/return", rentalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberFirstname").value("Thanos"));
    }

    @Test
    void returnRental_whenNotFound_shouldReturn404() throws Exception {
        when(rentalService.returnRental(any()))
                .thenThrow(new EntityNotFoundException("Rental", "Not found"));

        mockMvc.perform(put("/api/rentals/{uuid}/return", rentalId))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnRental_whenAlreadyReturned_shouldReturn400() throws Exception {
        when(rentalService.returnRental(any()))
                .thenThrow(new EntityInvalidArgumentException("Rental", "Already returned"));

        mockMvc.perform(put("/api/rentals/{uuid}/return", rentalId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRental_whenExists_shouldReturn200() throws Exception {
        when(rentalService.getRentalByUuid(any())).thenReturn(rental);
        when(rentalMapper.mapToRentalReadOnlyDTO(any())).thenReturn(rentalReadOnlyDTO);

        mockMvc.perform(get("/api/rentals/{uuid}", rentalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Animal Farm"));
    }

    @Test
    void getRental_whenNotFound_shouldReturn404() throws Exception {
        when(rentalService.getRentalByUuid(any()))
                .thenThrow(new EntityNotFoundException("Rental", "Not found"));

        mockMvc.perform(get("/api/rentals/{uuid}", rentalId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRentals_shouldReturn200WithPage() throws Exception {
        Page<Rental> page = new PageImpl<>(List.of(rental), PageRequest.of(0, 10), 1);
        when(rentalService.getRentalsPaginated(any())).thenReturn(page);
        when(rentalMapper.mapToRentalReadOnlyDTO(any())).thenReturn(rentalReadOnlyDTO);

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bookTitle").value("Animal Farm"));
    }

    @Test
    void getRentalsByMember_whenMemberExists_shouldReturn200() throws Exception {
        when(rentalService.getRentalsByMemberUuid(any())).thenReturn(List.of(rental));
        when(rentalMapper.mapToRentalReadOnlyDTO(any())).thenReturn(rentalReadOnlyDTO);

        mockMvc.perform(get("/api/rentals/member/{memberUuid}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Animal Farm"));
    }

    @Test
    void getRentalsByMember_whenMemberNotFound_shouldReturn404() throws Exception {
        when(rentalService.getRentalsByMemberUuid(any()))
                .thenThrow(new EntityNotFoundException("Member", "Not found"));

        mockMvc.perform(get("/api/rentals/member/{memberUuid}", memberId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActiveRentals_shouldReturn200WithPage() throws Exception {
        Page<Rental> page = new PageImpl<>(List.of(rental), PageRequest.of(0, 10), 1);
        when(rentalService.getActiveRentalsPaginated(any())).thenReturn(page);
        when(rentalMapper.mapToRentalReadOnlyDTO(any())).thenReturn(rentalReadOnlyDTO);

        mockMvc.perform(get("/api/rentals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bookTitle").value("Animal Farm"));
    }
}