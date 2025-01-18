package com.facci.inventario.dominio;

import com.facci.inventario.enums.TipoOperacion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArticuloHistorial extends EntidadBase {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Articulo articulo;

    @Column(nullable = false)
    private String codigoInterno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOperacion tipoOperacion;

    @Column(nullable = false)
    private String descripcion;
}
