package com.library.management.security;

import com.library.management.authentication.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = User.builder()
                .username("admin")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    /** Captures the exception the filter handed to the entry point. */
    private AuthenticationException capturedRejection() throws Exception {
        ArgumentCaptor<AuthenticationException> captor =
                ArgumentCaptor.forClass(AuthenticationException.class);
        verify(authenticationEntryPoint).commence(eq(request), eq(response), captor.capture());
        return captor.getValue();
    }

    @Test
    void doFilterInternal_whenNoAuthorizationHeader_shouldContinueFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractSubject(any());
    }

    @Test
    void doFilterInternal_whenInvalidAuthorizationHeader_shouldContinueFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractSubject(any());
    }

    @Test
    void doFilterInternal_whenValidToken_shouldSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractSubject("validtoken")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validtoken", userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(authenticationEntryPoint);
    }

    @Test
    void doFilterInternal_whenExpiredToken_shouldRejectWithoutContinuing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredtoken");
        when(jwtService.extractSubject("expiredtoken")).thenThrow(ExpiredJwtException.class);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(capturedRejection()).isInstanceOf(CredentialsExpiredException.class);
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_whenTokenDoesNotMatchUser_shouldRejectWithoutContinuing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(jwtService.extractSubject("invalidtoken")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.isTokenValid("invalidtoken", userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(capturedRejection()).isInstanceOf(BadCredentialsException.class);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_whenUsernameIsNull_shouldContinueFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractSubject("validtoken")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void doFilterInternal_whenMalformedToken_shouldRejectWithoutContinuing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(jwtService.extractSubject("invalidtoken"))
                .thenThrow(new io.jsonwebtoken.MalformedJwtException("Invalid"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(capturedRejection()).isInstanceOf(BadCredentialsException.class);
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_whenGenericException_shouldRejectWithoutContinuing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractSubject("token")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin"))
                .thenThrow(new RuntimeException("Unexpected"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(capturedRejection()).isInstanceOf(AuthenticationServiceException.class);
        verifyNoInteractions(filterChain);
    }
}
