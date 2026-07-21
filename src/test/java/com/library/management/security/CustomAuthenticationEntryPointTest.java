package com.library.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint entryPoint;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        entryPoint = new CustomAuthenticationEntryPoint(new ObjectMapper());
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @Test
    void commence_shouldSetStatus401() throws Exception {
        AuthenticationException exception = mock(AuthenticationException.class);
        when(exception.getMessage()).thenReturn("Unauthorized");

        entryPoint.commence(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_shouldSetContentType() throws Exception {
        AuthenticationException exception = mock(AuthenticationException.class);
        when(exception.getMessage()).thenReturn("Unauthorized");

        entryPoint.commence(request, response, exception);

        verify(response).setContentType("application/json; charset=UTF-8");
    }

    @Test
    void commence_shouldWriteErrorResponse() throws Exception {
        AuthenticationException exception = mock(AuthenticationException.class);
        when(exception.getMessage()).thenReturn("Unauthorized");

        entryPoint.commence(request, response, exception);

        assertThat(stringWriter.toString()).contains("UNAUTHORIZED");
    }
}