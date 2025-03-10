package com.facci.comun.dto;

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
    private String nombreUsuarioEncargado;

}
