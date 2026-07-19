package com.library.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.authentication.AuthenticationService;
import com.library.management.dto.AuthenticationRequestDTO;
import com.library.management.dto.AuthenticationResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void authenticate_whenValidCredentials_shouldReturn200WithToken() throws Exception {
        AuthenticationRequestDTO dto = new AuthenticationRequestDTO("admin", "admin123!");
        AuthenticationResponseDTO responseDTO = new AuthenticationResponseDTO("eyJhbGciOiJIUzI1NiJ9...");

        when(authenticationService.authenticate(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiJ9..."));
    }

    @Test
    void authenticate_whenInvalidCredentials_shouldReturn500() throws Exception {
        AuthenticationRequestDTO dto = new AuthenticationRequestDTO("admin", "wrongpassword");

        when(authenticationService.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
}