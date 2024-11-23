package com.facci.inventario.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Secuencial extends EntidadBase {
    @Column(nullable = false, unique = true)
    private String tipo;

    @Column(nullable = false, unique = true)
    private String prefijo;

    @Column(nullable = false)
    private Integer ultimoValor;

}
