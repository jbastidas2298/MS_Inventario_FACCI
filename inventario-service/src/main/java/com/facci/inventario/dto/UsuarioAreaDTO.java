package com.facci.inventario.dto;

import com.facci.inventario.enums.TipoRelacion;
import lombok.*;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UsuarioAreaDTO {
    Long id;
    String nombre;
    TipoRelacion tipoRelacion;

}
