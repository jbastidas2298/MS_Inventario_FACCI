package com.facci.configuracion.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
public class Area extends EntidadBase {

    private String nombreArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_encargado_id", nullable = false)
    @JsonIgnore
    private Usuario usuarioEncargado;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private boolean bodega;

    public Area(String nombreArea, Usuario usuarioEncargado, boolean bodega) {
        this.nombreArea = nombreArea;
        this.usuarioEncargado = usuarioEncargado;
        this.bodega = bodega;
    }
}
