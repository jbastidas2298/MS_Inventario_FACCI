package com.facci.inventario.monitoreo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

public class UtilidadesSeguridadReactiva {

    public static Mono<Authentication> authentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .defaultIfEmpty(null);
    }

    public static Mono<Boolean> esUsuarioAnonimo() {
        return authentication()
                .map(auth -> auth == null || "anonymousUser".equals(auth.getName()));
    }

    public static Mono<Boolean> esUsuarioIdentificado() {
        return esUsuarioAnonimo().map(isAnonymous -> !isAnonymous);
    }

    public static Mono<String> nombreUsuarioEnSesion() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> {
                    System.out.println("Authentication en contexto: " + auth);
                    return auth.getName();
                })
                .defaultIfEmpty("Sistema");
    }

    private UtilidadesSeguridadReactiva() {
    }
}
