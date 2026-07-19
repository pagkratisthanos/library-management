package com.library.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.AddressInsertDTO;
import com.library.management.dto.AddressReadOnlyDTO;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberReadOnlyDTO;
import com.library.management.dto.MemberUpdateDTO;
import com.library.management.mapper.MemberMapper;
import com.library.management.model.Member;
import com.library.management.service.IMemberService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IMemberService memberService;

    @MockitoBean
    private MemberMapper memberMapper;

    private Member member;
    private MemberReadOnlyDTO memberReadOnlyDTO;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();

        member = new Member();
        member.setFirstname("Thanos");
        member.setLastname("Pagkratis");
        member.setEmail("thanos@example.com");
        member.setPhoneNumber("6912345678");

        AddressReadOnlyDTO addressReadOnlyDTO = new AddressReadOnlyDTO(
                UUID.randomUUID(), "Ermou", "10", "Athens", "Greece", "10563"
        );

        memberReadOnlyDTO = new MemberReadOnlyDTO(
                memberId, addressReadOnlyDTO, "Thanos", "Pagkratis",
                "thanos@example.com", "6912345678",
                LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );
    }

    private AddressInsertDTO createAddressDTO() {
        return new AddressInsertDTO("Ermou", "10", "Athens", "Greece", "10563");
    }

    @Test
    void saveMember_whenValidData_shouldReturn201() throws Exception {
        MemberInsertDTO dto = new MemberInsertDTO(
                createAddressDTO(), "Thanos", "Pagkratis", "6912345678",
                "thanos@example.com", LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );

        when(memberService.saveMember(any())).thenReturn(member);
        when(memberMapper.mapToMemberReadOnlyDTO(any())).thenReturn(memberReadOnlyDTO);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("Thanos"))
                .andExpect(jsonPath("$.email").value("thanos@example.com"));
    }

    @Test
    void saveMember_whenEmailExists_shouldReturn409() throws Exception {
        MemberInsertDTO dto = new MemberInsertDTO(
                createAddressDTO(), "Thanos", "Pagkratis", "6912345678",
                "thanos@example.com", LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );

        when(memberService.saveMember(any()))
                .thenThrow(new EntityAlreadyExistsException("Member", "Already exists"));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateMember_whenExists_shouldReturn200() throws Exception {
        MemberUpdateDTO dto = new MemberUpdateDTO(
                createAddressDTO(), "Updated", "Pagkratis", "6912345678",
                "thanos@example.com", LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );

        when(memberService.updateMember(any(), any())).thenReturn(member);
        when(memberMapper.mapToMemberReadOnlyDTO(any())).thenReturn(memberReadOnlyDTO);

        mockMvc.perform(put("/api/members/{uuid}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Thanos"));
    }

    @Test
    void updateMember_whenNotFound_shouldReturn404() throws Exception {
        MemberUpdateDTO dto = new MemberUpdateDTO(
                createAddressDTO(), "Updated", "Pagkratis", "6912345678",
                "thanos@example.com", LocalDate.of(1990, 1, 1), LocalDate.of(2024, 1, 1)
        );

        when(memberService.updateMember(any(), any()))
                .thenThrow(new EntityNotFoundException("Member", "Not found"));

        mockMvc.perform(put("/api/members/{uuid}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMember_whenExists_shouldReturn204() throws Exception {
        doNothing().when(memberService).deleteMemberByUuid(any());

        mockMvc.perform(delete("/api/members/{uuid}", memberId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteMember_whenNotFound_shouldReturn404() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Member", "Not found"))
                .when(memberService).deleteMemberByUuid(any());

        mockMvc.perform(delete("/api/members/{uuid}", memberId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMember_whenHasActiveRentals_shouldReturn400() throws Exception {
        org.mockito.Mockito.doThrow(new EntityInvalidArgumentException("Member", "Has active rentals"))
                .when(memberService).deleteMemberByUuid(any());

        mockMvc.perform(delete("/api/members/{uuid}", memberId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMember_whenExists_shouldReturn200() throws Exception {
        when(memberService.getMemberByUUIDDeletedFalse(any())).thenReturn(member);
        when(memberMapper.mapToMemberReadOnlyDTO(any())).thenReturn(memberReadOnlyDTO);

        mockMvc.perform(get("/api/members/{uuid}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Thanos"))
                .andExpect(jsonPath("$.email").value("thanos@example.com"));
    }

    @Test
    void getMember_whenNotFound_shouldReturn404() throws Exception {
        when(memberService.getMemberByUUIDDeletedFalse(any()))
                .thenThrow(new EntityNotFoundException("Member", "Not found"));

        mockMvc.perform(get("/api/members/{uuid}", memberId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMembers_shouldReturn200WithPage() throws Exception {
        Page<Member> page = new PageImpl<>(List.of(member), PageRequest.of(0, 10), 1);
        when(memberService.getMembersPaginatedAndDeletedFalse(any())).thenReturn(page);
        when(memberMapper.mapToMemberReadOnlyDTO(any())).thenReturn(memberReadOnlyDTO);

        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstname").value("Thanos"));
    }
}