package com.library.management.security;

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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Stateless JWT security. There is no session and no login form: every request carries its own
 * proof of identity in the Authorization header, or it is rejected.
 *
 * <p>Authorization is by capability rather than by role. A role is a bag of capabilities, so a new
 * role can be introduced without touching this file, as long as it is granted existing
 * capabilities.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * The matchers are evaluated top to bottom and the first match wins, so order is part of the
     * meaning. The rule for {@code /users/me/password} has to stay above the {@code /users/**}
     * rules: moved below them, a user without MANAGE_USERS could no longer change their own
     * password.
     */
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
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

    /** Only the React development server and the nginx container are allowed to call the API. */
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
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
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

}