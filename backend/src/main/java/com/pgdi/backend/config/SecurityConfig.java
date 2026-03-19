package com.pgdi.backend.config;

import com.pgdi.backend.security.SecurityFilter;
import com.pgdi.backend.service.AuthorizationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final AuthorizationService authorizationService;

    public SecurityConfig(SecurityFilter securityFilter, AuthorizationService authorizationService) {
        this.securityFilter = securityFilter;
        this.authorizationService = authorizationService;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        provider.setUserDetailsService(authorizationService);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // Auth
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers("/error").permitAll()
                // Rotas autenticadas
                .requestMatchers(HttpMethod.GET, "/filas").authenticated()
                .requestMatchers(HttpMethod.GET, "/tipos").authenticated()
                // Rotas admin
                .requestMatchers(HttpMethod.POST, "/auth/register").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/usuarios/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/filas/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/filas/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/documentos/*/status").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/documentos/**").hasAuthority("ROLE_ADMIN")
                // Rotas de documento
                .requestMatchers(HttpMethod.POST, "/documentos/upload").authenticated()
                .requestMatchers(HttpMethod.GET, "/documentos/fila").authenticated()
                .requestMatchers(HttpMethod.GET, "/documentos/arquivo/**").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Token invalido\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Acesso negado\"}");
                })
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
        "http://localhost:4200",  // desenvolvimento local
        "http://localhost"        // Docker (porta 80)
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    }
}