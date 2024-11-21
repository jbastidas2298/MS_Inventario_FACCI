package com.facci.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationManager authenticationManager;

    public SecurityContextRepository(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
        // No es necesario guardar el contexto en sistemas sin estado
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange swe) {
        String path = swe.getRequest().getPath().toString();
        String authHeader = swe.getRequest().getHeaders().getFirst("Authorization");
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/configuracion/login")) {
            return Mono.justOrEmpty(null); // Retorna un contexto vacío explícitamente
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
            return authenticationManager.authenticate(auth)
                    .map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }
}
