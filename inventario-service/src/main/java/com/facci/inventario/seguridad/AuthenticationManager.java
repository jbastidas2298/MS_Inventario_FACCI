package com.facci.inventario.seguridad;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTUtil jwtUtil;
    public AuthenticationManager(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        // Validar el token
        if (!jwtUtil.validateToken(token)) {
            return Mono.empty();
        }

        String username = jwtUtil.getUsernameFromToken(token);
        List<String> roles = jwtUtil.getRolesFromToken(token);

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                username, null, authorities);
        return Mono.just(auth);
    }
}
