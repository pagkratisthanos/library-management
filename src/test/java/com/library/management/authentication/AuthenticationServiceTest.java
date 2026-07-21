package com.library.management.authentication;

import com.library.management.dto.AuthenticationRequestDTO;
import com.library.management.dto.AuthenticationResponseDTO;
import com.library.management.model.Role;
import com.library.management.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ADMIN");

        user = new User();
        user.setUsername("admin");
        user.setPassword("password");
        role.addUser(user);
    }

    @Test
    void authenticate_whenValidCredentials_shouldReturnToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken("admin", "ADMIN")).thenReturn("jwt-token");

        AuthenticationRequestDTO dto = new AuthenticationRequestDTO("admin", "admin123!");
        AuthenticationResponseDTO response = authenticationService.authenticate(dto);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt-token");
    }

    @Test
    void authenticate_whenInvalidCredentials_shouldThrowException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthenticationRequestDTO dto = new AuthenticationRequestDTO("admin", "wrongpassword");

        assertThatThrownBy(() -> authenticationService.authenticate(dto))
                .isInstanceOf(BadCredentialsException.class);
    }
}