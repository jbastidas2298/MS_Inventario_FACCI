package com.facci.inventario.dto;

import com.facci.inventario.enums.EstadoArticulo;
import com.facci.inventario.enums.GrupoActivo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ArticuloDTO {
    private long id;
    private String codigoOrigen;
    private String codigoInterno;
    private String nombre;
    private String descripcion;
    private String marca;
    private EstadoArticulo estado;
    private String observacion;
    private boolean asignarseArticulo;
    private String modelo;
    private String serie;
    private String ubicacion;
    private String seccion;
    private GrupoActivo grupoActivo;
}