package com.library.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.UserInsertDTO;
import com.library.management.dto.UserReadOnlyDTO;
import com.library.management.mapper.UserMapper;
import com.library.management.model.User;
import com.library.management.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private UserMapper userMapper;

    private User user;
    private UserReadOnlyDTO userReadOnlyDTO;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setUsername("admin");
        user.setPassword("$2a$10$hashedpassword");

        userReadOnlyDTO = new UserReadOnlyDTO(userId, "admin", "ADMIN");
    }

    @Test
    void saveUser_whenValidData_shouldReturn201() throws Exception {
        UserInsertDTO dto = new UserInsertDTO("admin", "Admin123!", 1L);

        when(userService.saveUser(any())).thenReturn(user);
        when(userMapper.mapToUserReadOnlyDTO(any())).thenReturn(userReadOnlyDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void saveUser_whenUsernameExists_shouldReturn409() throws Exception {
        UserInsertDTO dto = new UserInsertDTO("admin", "Admin123!", 1L);

        when(userService.saveUser(any()))
                .thenThrow(new EntityAlreadyExistsException("User", "Already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void saveUser_whenRoleNotFound_shouldReturn400() throws Exception {
        UserInsertDTO dto = new UserInsertDTO("admin", "Admin123!", 999L);

        when(userService.saveUser(any()))
                .thenThrow(new EntityInvalidArgumentException("Role", "Not found"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_whenExists_shouldReturn200() throws Exception {
        when(userService.getUserByUuidDeletedFalse(any())).thenReturn(user);
        when(userMapper.mapToUserReadOnlyDTO(any())).thenReturn(userReadOnlyDTO);

        mockMvc.perform(get("/api/users/{uuid}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void getUser_whenNotFound_shouldReturn404() throws Exception {
        when(userService.getUserByUuidDeletedFalse(any()))
                .thenThrow(new EntityNotFoundException("User", "Not found"));

        mockMvc.perform(get("/api/users/{uuid}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser_whenDeleted_shouldReturn404() throws Exception {
        when(userService.getUserByUuidDeletedFalse(any()))
                .thenThrow(new EntityNotFoundException("User", "Not found"));

        mockMvc.perform(get("/api/users/{uuid}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_whenExists_shouldReturn204() throws Exception {
        doNothing().when(userService).deleteUserByUuid(any());

        mockMvc.perform(delete("/api/users/{uuid}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_whenNotFound_shouldReturn404() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("User", "Not found"))
                .when(userService).deleteUserByUuid(any());

        mockMvc.perform(delete("/api/users/{uuid}", userId))
                .andExpect(status().isNotFound());
    }
}