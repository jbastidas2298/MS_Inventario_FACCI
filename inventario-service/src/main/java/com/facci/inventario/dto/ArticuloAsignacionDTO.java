package com.facci.inventario.dto;

import com.facci.inventario.enums.TipoRelacion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticuloAsignacionDTO {

    private Long idUsuario;
    private String nombreAsignado;
    private Long idArticulo;
    private String codigoInterno;
    private String codigoOrigen;
    private LocalDateTime fechaAsignacion;
    private TipoRelacion tipoRelacion;

}
