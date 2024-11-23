package com.facci.configuracion.dominio;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
public class Area extends EntidadBase {

    private String nombreArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_encargado_id", nullable = false)
    private Usuario usuarioEncargado;

    public Area(String nombreArea, Usuario usuarioEncargado ) {
        this.nombreArea = nombreArea;
        this.usuarioEncargado = usuarioEncargado;
    }
}
