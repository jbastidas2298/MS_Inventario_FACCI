package com.facci.inventario.dominio;

import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.enums.EstadoArticulo;
import com.facci.inventario.enums.GrupoActivo;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
public class Articulo extends EntidadBase {

    private String codigoOrigen;

    private String codigoInterno;

    private String nombre;

    private String marca;

    private String modelo;

    private String serie;

    private String ubicacion;

    private String seccion;

    private String observacion;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoArticulo estado;

    @Enumerated(EnumType.STRING)
    private GrupoActivo grupoActivo;

    public Articulo(String codigoOrigen, String codigoInterno, String nombre, String descripcion, String marca, String modelo, String serie,
                    String ubicacion, String seccion, String observacion, EstadoArticulo estado, GrupoActivo grupoActivo) {
        this.codigoOrigen = codigoOrigen;
        this.codigoInterno = codigoInterno;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.modelo = modelo;
        this.serie = serie;
        this.ubicacion = ubicacion;
        this.seccion = seccion;
        this.observacion = observacion;
        this.estado = estado != null ? estado : EstadoArticulo.DISPONIBLE;
        this.grupoActivo = grupoActivo;
    }

    public Articulo(ArticuloDTO articuloDTO) {
        this.codigoOrigen = articuloDTO.getCodigoOrigen();
        this.codigoInterno = articuloDTO.getCodigoInterno();
        this.nombre = articuloDTO.getNombre();
        this.descripcion = articuloDTO.getDescripcion();
        this.marca = articuloDTO.getMarca();
        this.modelo = articuloDTO.getModelo();
        this.serie = articuloDTO.getSerie();
        this.ubicacion = articuloDTO.getUbicacion();
        this.seccion = articuloDTO.getSeccion();
        this.observacion = articuloDTO.getObservacion();
        this.estado = articuloDTO.getEstado() != null ? articuloDTO.getEstado() : EstadoArticulo.DISPONIBLE;
        this.grupoActivo = articuloDTO.getGrupoActivo();
    }
}