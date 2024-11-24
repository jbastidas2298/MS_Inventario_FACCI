package com.facci.inventario.dominio;

import com.facci.inventario.enums.TipoOperacion;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArticuloHistorial extends EntidadBase {

    @Column(nullable = false)
    private Long idArticulo;

    @Column(nullable = false)
    private String codigoInterno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoOperacion tipoOperacion;

    @Column(nullable = false)
    private String descripcion;
}
