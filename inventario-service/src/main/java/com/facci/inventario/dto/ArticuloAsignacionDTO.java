package com.facci.inventario.dto;

import com.facci.comun.enums.TipoRelacion;
import com.facci.inventario.enums.EstadoArticulo;
import com.facci.inventario.enums.GrupoActivo;
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
    private EstadoArticulo estadoArticulo;
    private String nombreArticulo;
    private String marcaArticulo;
    private String serieArticulo;
    private String modeloArticulo;
    private String ubicacionArticulo;
    private String seccionArticulo;
    private GrupoActivo grupoActivo;
    private String descripcion;
}
