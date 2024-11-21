package com.facci.inventario.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public SecurityConfig(AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Desactiva CSRF
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Desactiva login basado en formularios
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Desactiva autenticación básica
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/swagger-ui/**",      // Recursos de Swagger UI
                                "/swagger-ui.html",    // Página principal de Swagger UI
                                "/webjars/**",         // Recursos estáticos de Swagger
                                "/v3/api-docs/**"
                                ).permitAll()
                        .anyExchange().authenticated() // Proteger todo lo demás
                )
                .build();
    }
}