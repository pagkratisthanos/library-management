package com.library.management.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Puts the current username and client IP into the logging context, so that they can be attached to
 * every log line written while handling a request, without each call site passing them along.
 * A log pattern picks them up with {@code %X{user}} and {@code %X{ip}}.
 *
 * <p>The context is tied to the thread, and threads are pooled and reused, so it must be cleared in
 * a {@code finally} block. Without that, the next request on the same thread would be logged under
 * the previous user's name.
 *
 * <p>Behind a proxy the connecting address is the proxy's, so {@code X-Forwarded-For} is preferred
 * where present and its first entry — the original client — is taken.
 */
@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String user = "anonymous";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) user = auth.getName();

            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp != null && !clientIp.isEmpty()) {
                clientIp = clientIp.split(",")[0].trim();
            } else {
                clientIp = request.getRemoteAddr();
            }
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }

            MDC.put("user", user);
            MDC.put("ip", clientIp);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}