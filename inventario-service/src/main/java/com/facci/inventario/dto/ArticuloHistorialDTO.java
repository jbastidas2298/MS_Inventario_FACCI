package com.facci.inventario.dto;

import com.facci.inventario.enums.TipoOperacion;
import lombok.*;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
public class ArticuloHistorialDTO {
    private String codigoInterno;
    private TipoOperacion tipoOperacion;
    private String descripcion;
}
