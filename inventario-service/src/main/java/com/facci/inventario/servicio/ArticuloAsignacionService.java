package com.facci.inventario.servicio;

import com.facci.inventario.Configuracion.ConfiguracionService;
import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.dto.ArticuloAsignacionDTO;
import com.facci.inventario.dto.UsuarioAreaDTO;
import com.facci.inventario.dto.UsuarioDTO;
import com.facci.inventario.enums.EnumCodigos;
import com.facci.inventario.enums.TipoOperacion;
import com.facci.inventario.enums.TipoRelacion;
import com.facci.inventario.handler.CustomException;
import com.facci.inventario.repositorio.ArticuloAsignacionRepositorio;
import com.facci.inventario.repositorio.ArticuloRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j

@Service
public class ArticuloAsignacionService {
    private final ArticuloAsignacionRepositorio articuloAsignacionRepositorio;
    private final ArticuloRepositorio articuloRepositorio;
    private final ArticuloHistorialService articuloHistorialService;
    private final UsuarioSesionService usuarioSesionService;
    private final ConfiguracionService configuracionService;

    public ArticuloAsignacionService(ArticuloAsignacionRepositorio articuloAsignacionRepositorio, ArticuloRepositorio articuloRepositorio, ArticuloHistorialService articuloHistorialService, UsuarioSesionService usuarioSesionService, ConfiguracionService configuracionService){
        this.articuloAsignacionRepositorio = articuloAsignacionRepositorio;
        this.articuloRepositorio = articuloRepositorio;
        this.articuloHistorialService = articuloHistorialService;
        this.usuarioSesionService = usuarioSesionService;
        this.configuracionService = configuracionService;
    }

    public List<ArticuloAsignacionDTO> asignarArticulos(Long idRelacionado, TipoRelacion tipoRelacion, List<Long> idsArticulos) {
        return idsArticulos.stream()
                .map(idArticulo -> {
                    ArticuloAsignacion asignacionExistente = articuloAsignacionRepositorio.findByArticuloId(idArticulo)
                            .orElseThrow(() -> new RuntimeException("No se encontró una asignación para el artículo con ID: " + idArticulo));

                    asignacionExistente.setIdUsuario(idRelacionado);
                    asignacionExistente.setTipoRelacion(tipoRelacion);
                    asignacionExistente.setFechaAsignacion(LocalDateTime.now());
                    var usuarioArea = configuracionService.consultarUsuarioArea(idRelacionado,tipoRelacion);
                    UsuarioDTO usuarioDTO = new UsuarioDTO();
                    usuarioDTO.setNombreCompleto(usuarioArea.getNombre());
                    usuarioDTO.setId(usuarioArea.getId());

                    articuloHistorialService.registrarEvento(asignacionExistente.getArticulo(), TipoOperacion.REASIGNACION, null, usuarioDTO);

                    articuloAsignacionRepositorio.save(asignacionExistente);

                    Articulo articulo = asignacionExistente.getArticulo();
                    ArticuloAsignacionDTO dto = new ArticuloAsignacionDTO();
                    dto.setIdUsuario(asignacionExistente.getIdUsuario());
                    dto.setNombreAsignado(usuarioArea.getNombre());
                    dto.setIdArticulo(articulo.getId());
                    dto.setCodigoInterno(articulo.getCodigoInterno());
                    dto.setCodigoOrigen(articulo.getCodigoOrigen());
                    dto.setFechaAsignacion(asignacionExistente.getFechaAsignacion());
                    dto.setTipoRelacion(asignacionExistente.getTipoRelacion());

                    return dto;
                })
                .collect(Collectors.toList());
    }



    public List<ArticuloAsignacion> reasignarArticulosTodos(
            Long idUsuarioActual,
            TipoRelacion tipoRelacionActual,
            Long idUsuarioNuevo,
            TipoRelacion tipoRelacionNuevo,
            String descripcion) {

        List<ArticuloAsignacion> asignacionesActuales = articuloAsignacionRepositorio.findByIdUsuarioAndTipoRelacion(
                idUsuarioActual, tipoRelacionActual);

        if (asignacionesActuales.isEmpty()) {
            throw new CustomException(EnumCodigos.ASIGNACIONES_NO_ENCONTRADAS);
        }

        return asignacionesActuales.stream()
                .map(asignacion -> {
                    asignacion.setIdUsuario(idUsuarioNuevo);
                    asignacion.setTipoRelacion(tipoRelacionNuevo);

                    ArticuloAsignacion asignacionActualizada = articuloAsignacionRepositorio.save(asignacion);

                    UsuarioDTO usuarioDTO = usuarioSesionService.usuarioCompleto();
                    articuloHistorialService.registrarEvento(
                            asignacion.getArticulo(),
                            TipoOperacion.REASIGNACION,
                            String.format(
                                    "Artículo reasignado de usuario/área %d (%s) a usuario/área %d (%s). Motivo: %s",
                                    idUsuarioActual, tipoRelacionActual, idUsuarioNuevo, tipoRelacionNuevo, descripcion),
                            usuarioDTO
                    );

                    return asignacionActualizada;
                })
                .collect(Collectors.toList());
    }


    public List<ArticuloAsignacion> reasignarArticulos(List<Long> idsArticulos, Long idUsuarioNuevo, String descripcion) {
        UsuarioDTO usuarioDTO = usuarioSesionService.usuarioCompleto();

        return idsArticulos.stream()
                .map(idArticulo -> {
                    ArticuloAsignacion asignacionActual = articuloAsignacionRepositorio.findByArticuloId(idArticulo)
                            .orElseThrow(() -> new CustomException(EnumCodigos.ASIGNACIONES_NO_ENCONTRADAS));

                    Long idUsuarioAnterior = asignacionActual.getIdUsuario();
                    asignacionActual.setIdUsuario(idUsuarioNuevo);

                    ArticuloAsignacion asignacionActualizada = articuloAsignacionRepositorio.save(asignacionActual);
                    articuloHistorialService.registrarEvento(
                            asignacionActualizada.getArticulo(),
                            TipoOperacion.REASIGNACION,
                            String.format("Artículo reasignado de usuario %d a usuario %d. Motivo: %s", idUsuarioAnterior, idUsuarioNuevo, descripcion),
                            usuarioDTO
                    );

                    return asignacionActualizada;
                })
                .collect(Collectors.toList());
    }


    public List<Long> obtenerIdsArticulosAsignadosAlUsuario() {
        UsuarioDTO usuarioSesion = usuarioSesionService.usuarioCompleto();
        Long idUsuario = usuarioSesion.getId();

        List<ArticuloAsignacion> asignaciones = articuloAsignacionRepositorio.findByIdUsuario(idUsuario);

        if (asignaciones.isEmpty()) {
            log.info("No se encontraron asignaciones para el usuario con ID: {}", idUsuario);
            return Collections.emptyList();
        }
        return asignaciones.stream()
                .map(asignacion -> asignacion.getArticulo().getId())
                .collect(Collectors.toList());
    }

    public List<ArticuloAsignacionDTO> obtenerAsignacionesConDetalles() {
        List<ArticuloAsignacion> asignaciones = (List<ArticuloAsignacion>) articuloAsignacionRepositorio.findAll();
        var areaUsuarioAreaTodos = configuracionService.consultarUsuarioAreaTodos();

        Map<String, UsuarioAreaDTO> areaUsuarioAreaMap = areaUsuarioAreaTodos.stream()
                .collect(Collectors.toMap(
                        usuarioArea -> usuarioArea.getId() + "_" + usuarioArea.getTipoRelacion(),
                        usuarioArea -> usuarioArea
                ));

        return asignaciones.stream().map(asignacion -> {
            String clave = asignacion.getIdUsuario() + "_" + asignacion.getTipoRelacion();
            UsuarioAreaDTO areaUsuarioArea = areaUsuarioAreaMap.get(clave);
            Articulo articulo = asignacion.getArticulo();
            ArticuloAsignacionDTO dto = new ArticuloAsignacionDTO();
            dto.setIdUsuario(asignacion.getIdUsuario());
            dto.setNombreAsignado(areaUsuarioArea != null ? areaUsuarioArea.getNombre(): null);
            dto.setIdArticulo(articulo.getId());
            dto.setCodigoInterno(articulo.getCodigoInterno());
            dto.setCodigoOrigen(articulo.getCodigoOrigen());
            dto.setFechaAsignacion(asignacion.getFechaAsignacion());
            dto.setTipoRelacion(asignacion.getTipoRelacion());
            return dto;
        }).collect(Collectors.toList());
    }



    private Articulo obtenerArticuloPorId(Long idArticulo) {
        return articuloRepositorio.findById(idArticulo)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
    }
}
