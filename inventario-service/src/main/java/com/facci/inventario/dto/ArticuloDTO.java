package com.facci.inventario.dto;

import com.facci.inventario.enums.EstadoArticulo;
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
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipo;
    private String marca;
    private EstadoArticulo estado;
    private String observacion;
}