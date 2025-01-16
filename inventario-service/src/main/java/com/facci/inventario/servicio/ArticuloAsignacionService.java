package com.facci.inventario.servicio;

import com.facci.comun.dto.UsuarioAreaDTO;
import com.facci.comun.dto.UsuarioDTO;
import com.facci.comun.enums.EnumCodigos;
import com.facci.comun.enums.TipoRelacion;
import com.facci.comun.handler.CustomException;
import com.facci.inventario.Configuracion.ConfiguracionService;
import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.dto.ArticuloAsignacionDTO;
import com.facci.inventario.enums.TipoOperacion;
import com.facci.inventario.repositorio.ArticuloAsignacionRepositorio;
import com.facci.inventario.repositorio.ArticuloRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
                    ArticuloAsignacion asignacion = articuloAsignacionRepositorio.findByArticuloId(idArticulo)
                            .orElseGet(() -> {
                                Articulo articulo = articuloRepositorio.findById(idArticulo)
                                        .orElseThrow(() -> new RuntimeException("No se encontró el artículo con ID: " + idArticulo));

                                ArticuloAsignacion nuevaAsignacion = new ArticuloAsignacion();
                                nuevaAsignacion.setArticulo(articulo);
                                nuevaAsignacion.setIdUsuario(idRelacionado);
                                nuevaAsignacion.setTipoRelacion(tipoRelacion);
                                nuevaAsignacion.setFechaAsignacion(LocalDateTime.now());

                                return articuloAsignacionRepositorio.save(nuevaAsignacion);
                            });

                    asignacion.setIdUsuario(idRelacionado);
                    asignacion.setTipoRelacion(tipoRelacion);
                    asignacion.setFechaAsignacion(LocalDateTime.now());

                    var usuarioArea = configuracionService.consultarUsuarioArea(idRelacionado, tipoRelacion);
                    UsuarioDTO usuarioDTO = new UsuarioDTO();
                    usuarioDTO.setNombreCompleto(usuarioArea.getNombre());
                    usuarioDTO.setId(usuarioArea.getId());

                    articuloHistorialService.registrarEvento(asignacion.getArticulo(), TipoOperacion.REASIGNACION, null, usuarioDTO);

                    articuloAsignacionRepositorio.save(asignacion);

                    Articulo articulo = asignacion.getArticulo();
                    ArticuloAsignacionDTO dto = new ArticuloAsignacionDTO();
                    dto.setIdUsuario(asignacion.getIdUsuario());
                    dto.setNombreAsignado(usuarioArea.getNombre());
                    dto.setIdArticulo(articulo.getId());
                    dto.setCodigoInterno(articulo.getCodigoInterno());
                    dto.setCodigoOrigen(articulo.getCodigoOrigen());
                    dto.setFechaAsignacion(asignacion.getFechaAsignacion());
                    dto.setTipoRelacion(asignacion.getTipoRelacion());

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

    public List<ArticuloAsignacionDTO> obtenerAsignacionesConDetallesPRB() {
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

    public Page<ArticuloAsignacionDTO> obtenerAsignacionesConDetalles(Optional<Integer> page, Optional<Integer> size, Optional<String> filter) {
        log.info("Consultando asignacion");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        String filterValue = filter.orElse("").trim();
        Page<Articulo> paginaArticulos = articuloRepositorio.findByNombreContainingIgnoreCaseOrCodigoOrigenContainingIgnoreCase(
                filterValue, filterValue, pageable);

        Iterable<ArticuloAsignacion> iterableAsignaciones = articuloAsignacionRepositorio.findAll();
        List<ArticuloAsignacion> asignaciones = new ArrayList<>();
        iterableAsignaciones.forEach(asignaciones::add);
        var areaUsuarioAreaTodos = configuracionService.consultarUsuarioAreaTodos();

        Map<String, UsuarioAreaDTO> areaUsuarioAreaMap = areaUsuarioAreaTodos.stream()
                .collect(Collectors.toMap(
                        usuarioArea -> usuarioArea.getId() + "_" + usuarioArea.getTipoRelacion(),
                        usuarioArea -> usuarioArea
                ));

        Map<Long, ArticuloAsignacion> asignacionMap = asignaciones.stream()
                .collect(Collectors.toMap(
                        asignacion -> asignacion.getArticulo().getId(),
                        asignacion -> asignacion
                ));

        List<ArticuloAsignacionDTO> contenido = paginaArticulos.getContent().stream().map(articulo -> {
            ArticuloAsignacion asignacion = asignacionMap.get(articulo.getId());
            ArticuloAsignacionDTO dto = new ArticuloAsignacionDTO();
            dto.setIdArticulo(articulo.getId());
            dto.setCodigoInterno(articulo.getCodigoInterno());
            dto.setCodigoOrigen(articulo.getCodigoOrigen());
            dto.setFechaAsignacion(asignacion != null ? asignacion.getFechaAsignacion() : null);
            dto.setIdUsuario(asignacion != null ? asignacion.getIdUsuario() : null);
            dto.setTipoRelacion(asignacion != null ? asignacion.getTipoRelacion() : null);

            if (asignacion != null) {
                String clave = asignacion.getIdUsuario() + "_" + asignacion.getTipoRelacion();
                UsuarioAreaDTO areaUsuarioArea = areaUsuarioAreaMap.get(clave);
                dto.setNombreAsignado(areaUsuarioArea != null ? areaUsuarioArea.getNombre() : null);
            } else {
                dto.setNombreAsignado(null);
            }

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(contenido, pageable, paginaArticulos.getTotalElements());
    }


    private Articulo obtenerArticuloPorId(Long idArticulo) {
        return articuloRepositorio.findById(idArticulo)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
    }
}
