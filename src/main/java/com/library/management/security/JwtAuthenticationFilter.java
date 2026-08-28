package com.library.management.security;

import com.library.management.authentication.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Turns a valid Bearer token into an authenticated request.
 *
 * <p>A request without a token is passed straight through rather than rejected. That is deliberate:
 * this filter only establishes identity, and it is {@link SecurityConfiguration} that decides which
 * endpoints need one. Rejecting here would also block the public login and Swagger endpoints.
 *
 * <p>The authorities come from the database, not from the {@code role} claim inside the token, so a
 * role change takes effect on the next request instead of waiting for the token to expire.
 *
 * <p>A token that cannot be accepted is answered here, by handing the response to
 * {@link CustomAuthenticationEntryPoint}, instead of by throwing. An expired or forged token is an
 * ordinary event — a browser holding yesterday's cookie causes it — and throwing would let the
 * exception escape the filter chain, where the servlet container logs a full stack trace at ERROR
 * for something that is neither unexpected nor the server's fault.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authorizationHeader.substring(7);

        try {
            String username = jwtService.extractSubject(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtService.isTokenValid(jwt, userDetails)) {
                    throw new BadCredentialsException("Invalid token");
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (ExpiredJwtException e) {
            // more specific than JwtException, so it has to be caught first
            authenticationEntryPoint.commence(request, response,
                    new CredentialsExpiredException("Token has expired"));
            return;
        } catch (JwtException | IllegalArgumentException e) {
            authenticationEntryPoint.commence(request, response,
                    new BadCredentialsException("Invalid token"));
            return;
        } catch (AuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
            return;
        } catch (Exception e) {
            // anything else really is unexpected, so this one keeps its stack trace
            log.error("Unexpected error while validating the token", e);
            authenticationEntryPoint.commence(request, response,
                    new AuthenticationServiceException("Token validation failed"));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
