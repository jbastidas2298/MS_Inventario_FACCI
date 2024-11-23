package com.facci.inventario.dominio;

import com.facci.inventario.enums.TipoArchivo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticuloArchivo extends EntidadBase {
    @Column(nullable = false)
    private Long articuloId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoArchivo tipo;

    @Column(nullable = false)
    private String path;
}
