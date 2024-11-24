package com.facci.inventario.dominio;

import com.facci.inventario.enums.TipoRelacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticuloAsignacion extends EntidadBase {

    @Column(nullable = false)
    private Long idUsuario;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Articulo articulo;

    private LocalDateTime fechaAsignacion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    TipoRelacion tipoRelacion;
}
