package com.facci.inventario.dominio;

import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.enums.EstadoArticulo;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Articulo extends EntidadBase {

    private String codigoOrigen;

    private String codigoInterno;

    private String nombre;

    private String descripcion;

    private String marca;

    private String observacion;

    @Enumerated(EnumType.STRING)
    private EstadoArticulo estado;

    public Articulo(String codigoOrigen,String codigoInterno, String nombre, String descripcion, String marca, String observacion) {
        this.codigoOrigen = codigoOrigen;
        this.codigoInterno = codigoInterno;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.estado = EstadoArticulo.DISPONIBLE;
        this.observacion = observacion;
    }

    public Articulo(ArticuloDTO articuloDTO) {
        this.codigoOrigen = articuloDTO.getCodigoOrigen();
        this.codigoInterno = articuloDTO.getCodigoInterno();
        this.nombre = articuloDTO.getNombre();
        this.descripcion = articuloDTO.getDescripcion();
        this.marca = articuloDTO.getMarca();
        this.estado = articuloDTO.getEstado();
        this.observacion = articuloDTO.getObservacion();
    }
}