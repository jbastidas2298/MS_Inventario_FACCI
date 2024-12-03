package com.facci.configuracion.dto;

import com.facci.configuracion.dominio.Area;
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


    public AreaDTO(Area area) {
        this.id = area.getId();
        this.nombreArea = area.getNombreArea();
        this.nombreUsuarioEncargado = area.getUsuarioEncargado().getNombreCompleto();
        this.usuarioEncargadoId = area.getUsuarioEncargado().getId();
    }

}
