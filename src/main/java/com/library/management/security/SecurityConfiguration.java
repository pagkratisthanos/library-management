package com.library.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider authenticationProvider)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(req -> req
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/authenticate").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/authors/**").hasAuthority("VIEW_AUTHOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/authors/**").hasAuthority("EDIT_AUTHOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/authors/**").hasAuthority("EDIT_AUTHOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/authors/**").hasAuthority("DELETE_AUTHOR")
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**").hasAuthority("VIEW_BOOK")
                        .requestMatchers(HttpMethod.POST, "/api/v1/books/**").hasAuthority("EDIT_BOOK")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/books/**").hasAuthority("EDIT_BOOK")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasAuthority("DELETE_BOOK")
                        .requestMatchers(HttpMethod.GET, "/api/v1/members/**").hasAuthority("VIEW_MEMBER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/members/**").hasAuthority("EDIT_MEMBER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/members/**").hasAuthority("EDIT_MEMBER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/members/**").hasAuthority("DELETE_MEMBER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/copies/**").hasAuthority("VIEW_COPY")
                        .requestMatchers(HttpMethod.POST, "/api/v1/copies/**").hasAuthority("EDIT_COPY")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/copies/**").hasAuthority("EDIT_COPY")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/copies/**").hasAuthority("DELETE_COPY")
                        .requestMatchers(HttpMethod.GET, "/api/v1/rentals/**").hasAuthority("VIEW_RENTAL")
                        .requestMatchers(HttpMethod.POST, "/api/v1/rentals/**").hasAuthority("MANAGE_RENTAL")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/rentals/**").hasAuthority("MANAGE_RENTAL")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/me/password").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasAuthority("MANAGE_USERS")
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAuthority("MANAGE_USERS")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAuthority("MANAGE_USERS")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasAuthority("MANAGE_USERS")
                        .requestMatchers(HttpMethod.GET, "/api/v1/roles/**").hasAuthority("MANAGE_USERS")
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder,
                                                         UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(passwordEncoder);
        authenticationProvider.setUserDetailsService(userDetailsService);
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