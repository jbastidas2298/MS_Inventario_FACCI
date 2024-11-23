package com.facci.inventario.dominio;

import com.facci.inventario.repositorio.SecuencialRepositorio;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SecuencialInitializer {

    private final SecuencialRepositorio secuencialRepositorio;

    public SecuencialInitializer(SecuencialRepositorio secuencialRepositorio) {
        this.secuencialRepositorio = secuencialRepositorio;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        secuencialRepositorio.findByTipo("Articulo").orElseGet(() ->
                secuencialRepositorio.save(Secuencial.builder()
                        .prefijo("INV")
                        .ultimoValor(0)
                        .tipo("Articulo")
                        .build())
        );
    }
}
