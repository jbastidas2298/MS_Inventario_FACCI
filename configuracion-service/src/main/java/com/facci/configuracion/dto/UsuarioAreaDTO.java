package com.facci.configuracion.dto;

import com.facci.configuracion.enums.TipoRelacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UsuarioAreaDTO {
    Long id;
    String nombre;
    TipoRelacion tipoRelacion;
}
