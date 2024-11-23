package com.facci.inventario.map;

import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dto.ArticuloDTO;
import org.springframework.stereotype.Service;

@Service
public class ArticuloMapper {

    /**
     * Convierte un objeto ArticuloDTO a una entidad Articulo.
     * Este método utiliza un patrón Builder para mapear los campos de un DTO a una entidad persistente.
     *
     * @param dto el ArticuloDTO a convertir.
     * @return la entidad Articulo mapeada desde el DTO.
     */
    public Articulo mapToEntity(ArticuloDTO dto) {
        return Articulo.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .codigoOrigen(dto.getCodigoOrigen())
                .codigoInterno(dto.getCodigoInterno())
                .marca(dto.getMarca())
                .estado(dto.getEstado())
                .observacion(dto.getObservacion())
                .build();
    }

    /**
     * Convierte una entidad Articulo a un objeto ArticuloDTO.
     * Este método utiliza un patrón Builder para mapear los campos de una entidad persistente a un DTO.
     *
     * @param articulo la entidad Articulo a convertir.
     * @return el ArticuloDTO mapeado desde la entidad.
     */
    public ArticuloDTO mapToDto(Articulo articulo) {
        return ArticuloDTO.builder()
                .id(articulo.getId())
                .nombre(articulo.getNombre())
                .descripcion(articulo.getDescripcion())
                .codigoOrigen(articulo.getCodigoOrigen())
                .codigoInterno(articulo.getCodigoInterno())
                .marca(articulo.getMarca())
                .estado(articulo.getEstado())
                .observacion(articulo.getObservacion())
                .build();
    }
}
