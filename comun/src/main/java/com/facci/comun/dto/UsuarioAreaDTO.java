package com.facci.comun.dto;

import com.facci.comun.enums.TipoRelacion;
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
