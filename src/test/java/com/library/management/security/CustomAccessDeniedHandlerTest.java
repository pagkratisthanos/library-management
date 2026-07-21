package com.library.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {

    private CustomAccessDeniedHandler accessDeniedHandler;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        accessDeniedHandler = new CustomAccessDeniedHandler(new ObjectMapper());
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        when(request.getRequestURI()).thenReturn("/api/authors");
    }

    @Test
    void handle_shouldSetStatus403() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void handle_shouldSetContentType() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(request, response, exception);

        verify(response).setContentType("application/json; charset=UTF-8");
    }

    @Test
    void handle_shouldWriteErrorResponse() throws Exception {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        accessDeniedHandler.handle(request, response, exception);

        assertThat(stringWriter.toString()).contains("ACCESS_DENIED");
    }
}