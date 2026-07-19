package com.library.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.management.authentication.CustomUserDetailsService;
import com.library.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider authenticationProvider)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.POST, "/api/auth/authenticate").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/authors/**").hasAuthority("VIEW_AUTHOR")
                        .requestMatchers(HttpMethod.POST, "/api/authors/**").hasAuthority("EDIT_AUTHOR")
                        .requestMatchers(HttpMethod.PUT, "/api/authors/**").hasAuthority("EDIT_AUTHOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/authors/**").hasAuthority("DELETE_AUTHOR")
                        .requestMatchers(HttpMethod.GET, "/api/books/**").hasAuthority("VIEW_BOOK")
                        .requestMatchers(HttpMethod.POST, "/api/books/**").hasAuthority("EDIT_BOOK")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAuthority("EDIT_BOOK")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAuthority("DELETE_BOOK")
                        .requestMatchers(HttpMethod.GET, "/api/members/**").hasAuthority("VIEW_MEMBER")
                        .requestMatchers(HttpMethod.POST, "/api/members/**").hasAuthority("EDIT_MEMBER")
                        .requestMatchers(HttpMethod.PUT, "/api/members/**").hasAuthority("EDIT_MEMBER")
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasAuthority("DELETE_MEMBER")
                        .requestMatchers(HttpMethod.GET, "/api/copies/**").hasAuthority("VIEW_COPY")
                        .requestMatchers(HttpMethod.POST, "/api/copies/**").hasAuthority("EDIT_COPY")
                        .requestMatchers(HttpMethod.PUT, "/api/copies/**").hasAuthority("EDIT_COPY")
                        .requestMatchers(HttpMethod.DELETE, "/api/copies/**").hasAuthority("DELETE_COPY")
                        .requestMatchers(HttpMethod.GET, "/api/rentals/**").hasAuthority("VIEW_RENTAL")
                        .requestMatchers(HttpMethod.POST, "/api/rentals/**").hasAuthority("MANAGE_RENTAL")
                        .requestMatchers(HttpMethod.PUT, "/api/rentals/**").hasAuthority("MANAGE_RENTAL")
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority("MANAGE_USERS")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("MANAGE_USERS")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(myCustomAuthenticationEntryPoint())
                        .accessDeniedHandler(myCustomAccessDeniedHandler()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(passwordEncoder);
        authenticationProvider.setUserDetailsService(new CustomUserDetailsService(userRepository));
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint myCustomAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public AccessDeniedHandler myCustomAccessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper);
    }
}