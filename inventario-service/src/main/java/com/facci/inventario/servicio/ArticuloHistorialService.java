package com.facci.inventario.servicio;

import com.facci.comun.dto.UsuarioDTO;
import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloHistorial;
import com.facci.inventario.enums.TipoOperacion;
import com.facci.inventario.repositorio.ArticuloHistorialRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ArticuloHistorialService {

    private final ArticuloHistorialRepositorio articuloHistorialRepositorio;

    public ArticuloHistorialService(ArticuloHistorialRepositorio articuloHistorialRepositorio) {
        this.articuloHistorialRepositorio = articuloHistorialRepositorio;
    }

    public void registrarEvento(Articulo articulo, TipoOperacion tipoOperacion, String descripcion, UsuarioDTO usuarioDTO) {
        String descripcionFinal = Optional.ofNullable(descripcion)
                .orElseGet(() -> generarDescripcion(tipoOperacion, articulo, usuarioDTO));

        log.info("Registrando evento para artículo con ID {} y operación {}", articulo.getId(), tipoOperacion);

        ArticuloHistorial historial = ArticuloHistorial.builder()
                .idArticulo(articulo.getId())
                .codigoInterno(articulo.getCodigoInterno())
                .tipoOperacion(tipoOperacion)
                .descripcion(descripcionFinal)
                .build();

        articuloHistorialRepositorio.save(historial);
    }

    private String generarDescripcion(TipoOperacion tipoOperacion, Articulo articulo, UsuarioDTO usuarioDTO) {
        return switch (tipoOperacion) {
            case INGRESO -> "Se ingresó un nuevo artículo con código interno: " + articulo.getCodigoInterno() + " por el usuario " + usuarioDTO.getNombreCompleto();
            case ACTUALIZACION -> "Se actualizó el artículo con código interno: " + articulo.getCodigoInterno() + " por el usuario " + usuarioDTO.getNombreCompleto();
            case ASIGNACION -> "Se asignó el artículo con código interno: " + articulo.getCodigoInterno() + " a " + usuarioDTO.getNombreCompleto();
            case REASIGNACION -> "Se reasignó el artículo con código interno: " + articulo.getCodigoInterno() + " a " + usuarioDTO.getNombreCompleto();
            case ELIMINACION -> "Se eliminó el artículo con código interno: " + articulo.getCodigoInterno();
            default -> "Operación no especificada para el artículo con código interno: " + articulo.getCodigoInterno();
        };
    }
}
