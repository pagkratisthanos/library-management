package com.library.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Answers 401 in the same JSON shape the rest of the API uses.
 *
 * <p>Spring Security calls this when an unauthenticated request reaches a protected endpoint.
 * {@link JwtAuthenticationFilter} also calls it directly for a token it cannot accept, so that a
 * rejected token produces one 401 and one warning line rather than a stack trace.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {

        log.warn("Unauthenticated request to {} from IP={}. Message={}",
                request.getRequestURI(), request.getRemoteAddr(), e.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        new ErrorResponseDTO("UNAUTHORIZED", e.getMessage())
                )
        );
    }
}