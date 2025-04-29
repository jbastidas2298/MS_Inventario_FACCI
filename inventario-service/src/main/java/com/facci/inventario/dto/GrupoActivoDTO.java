package com.facci.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoActivoDTO {
    public long id;
    public String codigo;
    public String descripcion;
}
