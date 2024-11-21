package com.facci.inventario.dominio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioArticulo extends EntidadBase {

    @Column(nullable = false)
    private Long idUsuario;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Articulo articulo;

    private LocalDateTime fechaAsignacion;
}
