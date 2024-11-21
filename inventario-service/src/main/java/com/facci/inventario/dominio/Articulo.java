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

    private String codigo;

    private String nombre;

    private String descripcion;

    private String tipo;

    private String marca;

    private String observacion;

    @Enumerated(EnumType.STRING)
    private EstadoArticulo estado;

    public Articulo(String codigo, String nombre, String descripcion, String tipo, String marca, String modelo, String serialNumero,String observacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.marca = marca;
        this.estado = EstadoArticulo.DISPONIBLE;
        this.observacion = observacion;
    }

    public Articulo(ArticuloDTO articuloDTO) {
        this.codigo = articuloDTO.getCodigo();
        this.nombre = articuloDTO.getNombre();
        this.descripcion = articuloDTO.getDescripcion();
        this.tipo = articuloDTO.getTipo();
        this.marca = articuloDTO.getMarca();
        this.estado = articuloDTO.getEstado();
        this.observacion = articuloDTO.getObservacion();
    }
}