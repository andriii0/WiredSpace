package org.main.wiredspaceapi.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.main.wiredspaceapi.business.UserService;
import org.main.wiredspaceapi.security.jwt.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/users").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore((request, response, chain) -> {
                    HttpServletRequest httpRequest = (HttpServletRequest) request;

                    String authHeader = httpRequest.getHeader("Authorization");

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);

                        if (!token.isBlank()) {
                            try {
                                String username = jwtService.extractUsername(token);
                                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                                    var user = userService.findUserByEmail(username);
                                    if (user != null && jwtService.isTokenValid(token, user)) {
                                        var authToken = new UsernamePasswordAuthenticationToken(
                                                user, null, List.of()
                                        );
                                        SecurityContextHolder.getContext().setAuthentication(authToken);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }

                    chain.doFilter(request, response);
                }, UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }
}