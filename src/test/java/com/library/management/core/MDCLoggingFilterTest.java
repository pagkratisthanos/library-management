package com.library.management.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MDCLoggingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private MDCLoggingFilter mdcLoggingFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Test
    void doFilterInternal_whenAnonymousUser_shouldSetAnonymousInMDC() throws Exception {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        mdcLoggingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenAuthenticatedUser_shouldSetUsernameInMDC() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin", null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        mdcLoggingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenXForwardedForHeader_shouldUseProxyIP() throws Exception {
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");

        mdcLoggingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_whenIPv6Localhost_shouldConvertToIPv4() throws Exception {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");

        mdcLoggingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldClearMDCAfterRequest() throws Exception {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        mdcLoggingFilter.doFilterInternal(request, response, filterChain);

        assertThat(MDC.get("user")).isNull();
        assertThat(MDC.get("ip")).isNull();
    }
}