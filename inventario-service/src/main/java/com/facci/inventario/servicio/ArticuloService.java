package com.facci.inventario.servicio;

import com.facci.comun.dto.UsuarioDTO;
import com.facci.comun.enums.EnumCodigos;
import com.facci.comun.enums.EnumRolUsuario;
import com.facci.comun.enums.TipoRelacion;
import com.facci.comun.handler.CustomException;
import com.facci.inventario.Configuracion.ConfiguracionService;
import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.dto.*;
import com.facci.inventario.enums.*;
import com.facci.inventario.map.ArticuloMapper;
import com.facci.inventario.repositorio.ArticuloArchivoRepositorio;
import com.facci.inventario.repositorio.ArticuloAsignacionRepositorio;
import com.facci.inventario.repositorio.ArticuloHistorialRepositorio;
import com.facci.inventario.repositorio.ArticuloRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ArticuloService {

    private final ArticuloRepositorio articuloRepositorio;
    private final ArticuloMapper articuloMapper;
    private final SecuencialService secuencialService;
    private final ArticuloHistorialService articuloHistorialService;
    private final ConfiguracionService configuracionService;
    private final ArticuloAsignacionService articuloAsignacionService;
    private final UsuarioSesionService usuarioSesionService;
    private final ArticuloAsignacionRepositorio articuloAsignacionRepositorio;
    private final ArticuloArchivoRepositorio articuloArchivoRepositorio;
    private final ArticuloHistorialRepositorio articuloHistorialRepositorio;

    public ArticuloService(ArticuloRepositorio articuloRepositorio, ArticuloMapper articuloMapper, SecuencialService secuencialService, ArticuloHistorialService articuloHistorialService, ConfiguracionService configuracionService, ArticuloAsignacionService articuloAsignacionService, UsuarioSesionService usuarioSesionService, ArticuloAsignacionRepositorio articuloAsignacionRepositorio, ArticuloArchivoRepositorio articuloArchivoRepositorio, ArticuloHistorialRepositorio articuloHistorialRepositorio) {
        this.articuloRepositorio = articuloRepositorio;
        this.articuloMapper = articuloMapper;
        this.secuencialService = secuencialService;
        this.articuloHistorialService = articuloHistorialService;
        this.configuracionService = configuracionService;
        this.articuloAsignacionService = articuloAsignacionService;
        this.usuarioSesionService = usuarioSesionService;
        this.articuloAsignacionRepositorio = articuloAsignacionRepositorio;
        this.articuloArchivoRepositorio = articuloArchivoRepositorio;
        this.articuloHistorialRepositorio = articuloHistorialRepositorio;
    }


    public ArticuloDTO registrar(ArticuloDTO dto) {
        try {
            if(dto.getCodigoOrigen() != null){
                verificarArticuloExistente(dto.getCodigoOrigen());
            }

            String secuencial = secuencialService.generarSecuencial("Articulo");
            dto.setCodigoInterno(secuencial);

            Articulo articuloGuardado = guardarArticulo(dto);

            UsuarioDTO usuarioSesion = obtenerUsuarioSesion();
            registrarHistorialYAsignar(dto, articuloGuardado, usuarioSesion, TipoOperacion.INGRESO);

            log.debug("Artículo registrado: {}", articuloGuardado.getNombre());
            return articuloMapper.mapToDto(articuloGuardado);
        }catch (Exception e){
            throw new CustomException(EnumCodigos.ARTICULO_ERROR_REGISTRAR);
        }

    }

    public ArticuloDTO actualizar(ArticuloDTO dto) {
        Articulo articuloExistente = obtenerArticuloPorId(dto.getId());
        actualizarDatosArticulo(articuloExistente, dto);

        Articulo articuloActualizado = articuloRepositorio.save(articuloExistente);
        registrarHistorial(articuloActualizado, TipoOperacion.ACTUALIZACION);

        log.info("Artículo actualizado: {}", articuloActualizado.getNombre());
        return articuloMapper.mapToDto(articuloActualizado);
    }

    public void eliminar(Long id) {
        Articulo articulo = obtenerArticuloPorId(id);

        Optional<ArticuloAsignacion> articuloAsignacion = articuloAsignacionRepositorio.findByArticuloId(id);
        if (articuloAsignacion.isPresent()) {
            throw new CustomException(EnumCodigos.ARTICULO_ASIGNADO_NO_ELIMINABLE);
        }

        articuloRepositorio.delete(articulo);
        log.info("Artículo eliminado con id: {}", id);
    }

    public ArticuloDTO consultarArticulo(Long id) {
        Articulo articulo = obtenerArticuloPorId(id);
        return articuloMapper.mapToDto(articulo);
    }

    public Page<ArticuloDTO> consultarTodos(Optional<Integer> page, Optional<Integer> size, Optional<String> filter) {
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        String filterValue = filter.orElse("").trim();

        List<EnumRolUsuario> roles = usuarioSesionService.obtenerRolesActuales();

        if (roles.contains(EnumRolUsuario.ADMINISTRADOR)) {
            Page<Articulo> articulosPaginados = articuloRepositorio
                    .findByNombreContainingIgnoreCaseOrCodigoOrigenContainingIgnoreCase(filterValue, filterValue, pageable);
            log.info("Artículos consultados (ADMINISTRADOR): {}", articulosPaginados.getTotalElements());
            return articulosPaginados.map(articuloMapper::mapToDto);
        } else {
            List<Long> idsAsignadosUsuario = articuloAsignacionService.obtenerIdsArticulosAsignadosAlUsuario();
            Page<Articulo> articulosPaginados = articuloRepositorio
                    .findByIdInAndNombreContainingIgnoreCaseOrCodigoOrigenContainingIgnoreCase(
                            idsAsignadosUsuario, filterValue, filterValue, pageable);
            log.info("Artículos consultados para usuario: {}", articulosPaginados.getTotalElements());
            return articulosPaginados.map(articuloMapper::mapToDto);
        }
    }


    private void verificarArticuloExistente(String codigoOrigen) {
        if (articuloRepositorio.findByCodigoOrigen(codigoOrigen).isPresent()) {
            log.error("El artículo con código de origen '{}' ya existe.", codigoOrigen);
            throw new CustomException(EnumCodigos.ARTICULO_YA_EXISTE);
        }
    }

    private Articulo guardarArticulo(ArticuloDTO dto) {
        Articulo nuevoArticulo = new Articulo(dto);
        return articuloRepositorio.save(nuevoArticulo);
    }

    private UsuarioDTO obtenerUsuarioSesion() {
        String usuario = usuarioSesionService.obtenerUsuarioActual()
                .orElseThrow(() -> new CustomException(EnumCodigos.USUARIO_ASIGNAR_EN_SESION));

        UsuarioDTO usuarioSesion = configuracionService.buscarPorNombreUsuario(usuario);
        if (usuarioSesion == null) {
            log.error("No se encontró información del usuario en el servicio de configuración para '{}'.", usuario);
            throw new CustomException(EnumCodigos.USUARIO_ASIGNAR_EN_SESION);
        }
        return usuarioSesion;
    }

    private void registrarHistorialYAsignar(ArticuloDTO articuloDTO, Articulo articulo, UsuarioDTO usuario, TipoOperacion operacion) {
        articuloHistorialService.registrarEvento(articulo, operacion, operacion + " " + articulo.getObservacion(), usuario);
        if(articuloDTO.isAsignarseArticulo()){
            articuloAsignacionService.asignarArticulos(usuario.getId(), TipoRelacion.USUARIO, Collections.singletonList(articulo.getId()));
        }
    }

    private Articulo obtenerArticuloPorId(Long id) {
        return articuloRepositorio.findById(id)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
    }

    private void actualizarDatosArticulo(Articulo articulo, ArticuloDTO dto) {
        articulo.setNombre(dto.getNombre());
        articulo.setEstado(dto.getEstado());
        articulo.setMarca(dto.getMarca());
        articulo.setModelo(dto.getModelo());
        articulo.setSerie(dto.getSerie());
        articulo.setSeccion(dto.getSeccion());
        articulo.setUbicacion(dto.getUbicacion());
        articulo.setGrupoActivo(dto.getGrupoActivo());
        articulo.setObservacion(dto.getObservacion());
        articulo.setDescripcion(dto.getDescripcion());

    }

    private void registrarHistorial(Articulo articulo, TipoOperacion operacion) {
        UsuarioDTO usuarioDTO = usuarioSesionService.usuarioCompleto();
        articuloHistorialService.registrarEvento(articulo, operacion, null, usuarioDTO);
    }

    private List<Articulo> obtenerTodosLosArticulos() {
        return StreamSupport.stream(articuloRepositorio.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<Articulo> obtenerArticulosPorIds(List<Long> ids) {
        return StreamSupport.stream(articuloRepositorio.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<ArticuloDTO> mapearArticulosADTOs(List<Articulo> articulos) {
        return articulos.stream()
                .map(articuloMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public ArticuloDetalleDTO obtenerArticuloDetalle(Long articuloId) {
        ArticuloDetalleDTO detalleDTO = new ArticuloDetalleDTO();

        Articulo articulo = articuloRepositorio.findById(articuloId)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
        ArticuloDTO articuloDTO = articuloMapper.mapToDto(articulo);
        detalleDTO.setArticulo(articuloDTO);

        List<ArticuloArchivoDTO> archivos = articuloArchivoRepositorio.findByArticuloId(articuloId).stream()
                .map(archivo -> {
                    ArticuloArchivoDTO archivoDTO = new ArticuloArchivoDTO();
                    archivoDTO.setPath(archivo.getPath());
                    archivoDTO.setTipo(archivo.getTipo());
                    return archivoDTO;
                }).collect(Collectors.toList());
        detalleDTO.setArchivos(archivos);

        List<ArticuloHistorialDTO> historial = articuloHistorialRepositorio.findByIdArticulo(articuloId).stream()
                .map(historialEntity -> {
                    ArticuloHistorialDTO historialDTO = new ArticuloHistorialDTO();
                    historialDTO.setCodigoInterno(historialEntity.getCodigoInterno());
                    historialDTO.setTipoOperacion(historialEntity.getTipoOperacion());
                    historialDTO.setDescripcion(historialEntity.getDescripcion());
                    return historialDTO;
                }).collect(Collectors.toList());
        detalleDTO.setHistorial(historial);

        articuloAsignacionRepositorio.findByArticuloId(articuloId).ifPresent(asignacion -> {
            UsuarioDTO usuarioDTO = configuracionService.consultarUsuario(asignacion.getIdUsuario());
            detalleDTO.setUsuarioAsignado(usuarioDTO);
        });

        return detalleDTO;
    }
}
