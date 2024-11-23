package com.facci.configuracion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaDTO {
    long id;
    private String nombreArea;
    private Long usuarioEncargadoId;
}
