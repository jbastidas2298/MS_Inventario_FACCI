package com.facci.inventario.dominio;

import com.facci.inventario.enums.TipoArchivo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticuloArchivo extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Articulo articulo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoArchivo tipo;

    @Column(nullable = false)
    private String path;
}
