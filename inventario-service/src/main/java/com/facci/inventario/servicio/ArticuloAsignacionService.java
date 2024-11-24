package com.facci.inventario.servicio;

import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.enums.EnumErrores;
import com.facci.inventario.enums.TipoOperacion;
import com.facci.inventario.enums.TipoRelacion;
import com.facci.inventario.handler.CustomException;
import com.facci.inventario.repositorio.ArticuloAsignacionRepositorio;
import com.facci.inventario.repositorio.ArticuloRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@Service
public class ArticuloAsignacionService {
    private final ArticuloAsignacionRepositorio articuloAsignacionRepositorio;
    private final ArticuloRepositorio articuloRepositorio;
    private final ArticuloHistorialService articuloHistorialService;

    public ArticuloAsignacionService(ArticuloAsignacionRepositorio articuloAsignacionRepositorio, ArticuloRepositorio articuloRepositorio, ArticuloHistorialService articuloHistorialService){
        this.articuloAsignacionRepositorio = articuloAsignacionRepositorio;
        this.articuloRepositorio = articuloRepositorio;
        this.articuloHistorialService = articuloHistorialService;
    }

    public List<ArticuloAsignacion> asignarArticulos(Long idRelacionado, TipoRelacion tipoRelacion, List<Long> idsArticulos) {
        return idsArticulos.stream()
                .map(idArticulo -> {
                    Articulo articulo = obtenerArticuloPorId(idArticulo);
                    ArticuloAsignacion asignacion = ArticuloAsignacion.builder()
                            .idUsuario(idRelacionado)
                            .tipoRelacion(tipoRelacion)
                            .articulo(articulo)
                            .fechaAsignacion(LocalDateTime.now())
                            .build();
                    articuloHistorialService.registrarEvento(articulo, TipoOperacion.ASIGNACION,null);
                    return articuloAsignacionRepositorio.save(asignacion);
                })
                .collect(Collectors.toList());

    }

    public List<ArticuloAsignacion> reasignarArticulos(Long idUsuarioActual, Long idUsuarioNuevo, String descripcion) {
        List<ArticuloAsignacion> asignacionesActuales = articuloAsignacionRepositorio.findByIdUsuario(idUsuarioActual);

        if (asignacionesActuales.isEmpty()) {
            throw new CustomException(EnumErrores.ASIGNACIONES_NO_ENCONTRADAS);
        }

        return asignacionesActuales.stream()
                .map(asignacion -> {
                    asignacion.setIdUsuario(idUsuarioNuevo);

                    ArticuloAsignacion asignacionActualizada = articuloAsignacionRepositorio.save(asignacion);
                    articuloHistorialService.registrarEvento(
                            asignacion.getArticulo(),
                            TipoOperacion.REASIGNACION,
                            String.format("Artículo reasignado de usuario %d a usuario %d. Motivo: %s", idUsuarioActual, idUsuarioNuevo, descripcion)
                    );

                    return asignacionActualizada;
                })
                .collect(Collectors.toList());
    }

    public ArticuloAsignacion reasignarArticulo(Long idArticulo, Long idUsuarioNuevo, String descripcion) {
        ArticuloAsignacion asignacionActual = articuloAsignacionRepositorio.findByArticuloId(idArticulo)
                .orElseThrow(() -> new CustomException(EnumErrores.ASIGNACIONES_NO_ENCONTRADAS));

        Long idUsuarioAnterior = asignacionActual.getIdUsuario();
        asignacionActual.setIdUsuario(idUsuarioNuevo);

        ArticuloAsignacion asignacionActualizada = articuloAsignacionRepositorio.save(asignacionActual);
        articuloHistorialService.registrarEvento(
                asignacionActual.getArticulo(),
                TipoOperacion.REASIGNACION,
                String.format("Artículo reasignado de usuario %d a usuario %d. Motivo: %s", idUsuarioAnterior, idUsuarioNuevo, descripcion)
        );

        return asignacionActualizada;
    }


    private Articulo obtenerArticuloPorId(Long idArticulo) {
        return articuloRepositorio.findById(idArticulo)
                .orElseThrow(() -> new CustomException(EnumErrores.ARTICULO_NO_ENCONTRADO));
    }
}
