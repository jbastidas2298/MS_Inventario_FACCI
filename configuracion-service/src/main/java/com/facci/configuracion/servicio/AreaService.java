package com.facci.configuracion.servicio;

import com.facci.comun.enums.EnumCodigos;
import com.facci.comun.handler.CustomException;
import com.facci.configuracion.dominio.Area;
import com.facci.configuracion.dominio.Usuario;
import com.facci.configuracion.dto.AreaDTO;
import com.facci.configuracion.repositorio.AreaRepositorio;
import com.facci.configuracion.repositorio.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class AreaService {

    private final AreaRepositorio areaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public AreaService(AreaRepositorio areaRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.areaRepositorio = areaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Transactional
    public Map<String, Object> registrar(AreaDTO areaDTO) {
        log.info("Intentando registrar el área con nombre: {}", areaDTO.getNombreArea());
        areaRepositorio.findByNombreArea(areaDTO.getNombreArea())
                .ifPresent(existingArea -> {
                    log.error("Error al registrar el área. El área con nombre '{}' ya existe.", areaDTO.getNombreArea());
                    throw new CustomException(EnumCodigos.AREA_YA_EXISTE);
                });

        Usuario usuarioEncargado = usuarioRepositorio.findById(areaDTO.getUsuarioEncargadoId())
                .orElseThrow(() -> {
                    log.error("Error al registrar el área. Usuario encargado no encontrado con ID: {}", areaDTO.getUsuarioEncargadoId());
                    return new CustomException(EnumCodigos.USUARIO_NO_ENCONTRADO);
                });

        Area nuevaArea = new Area(areaDTO.getNombreArea(), usuarioEncargado);
        Area areaGuardada = areaRepositorio.save(nuevaArea);

        log.info("Área registrada con éxito: ID={}, Nombre={}, Usuario Encargado={}",
                areaGuardada.getId(),
                areaGuardada.getNombreArea(),
                usuarioEncargado.getNombreCompleto());

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", areaGuardada.getId());
        respuesta.put("nombreArea", areaGuardada.getNombreArea());
        respuesta.put("nombreUsuario", usuarioEncargado.getNombreCompleto());
        return respuesta;
    }




    @Transactional
    public Area actualizar(AreaDTO areaDTO) {
        log.info("Intentando actualizar el área con ID: {}", areaDTO.getId());
        Area areaExistente = areaRepositorio.findById(areaDTO.getId())
                .orElseThrow(() -> {
                    log.error("Error al actualizar. Área no encontrada con ID: {}", areaDTO.getId());
                    return new CustomException(EnumCodigos.AREA_NO_ENCONTRADA);
                });
        areaExistente.setNombreArea(areaDTO.getNombreArea());
        Usuario usuarioEncargado = usuarioRepositorio.findById(areaDTO.getUsuarioEncargadoId())
                .orElseThrow(() -> {
                    log.error("Error al actualizar el área. Usuario encargado no encontrado con ID: {}",areaDTO.getUsuarioEncargadoId());
                    return new CustomException(EnumCodigos.USUARIO_NO_ENCONTRADO);
                });

        areaExistente.setUsuarioEncargado(usuarioEncargado);
        Area areaActualizada = areaRepositorio.save(areaExistente);

        log.info("Área actualizada con éxito: ID={}, Nombre={}, Usuario Encargado={}",
                areaActualizada.getId(),
                areaActualizada.getNombreArea(),
                usuarioEncargado.getNombreCompleto());

        return areaActualizada;
    }


    @Transactional
    public void eliminar(Long id) {
        log.info("Intentando eliminar el área con ID: {}", id);
        Area area = areaRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al eliminar. Área no encontrada con ID: {}", id);
                    return new CustomException(EnumCodigos.AREA_NO_ENCONTRADA);
                });
        areaRepositorio.delete(area);
        log.info("Área eliminada con éxito. ID: {}", id);
    }

    public List<AreaDTO> consultarTodas() {
        log.info("Consultando todas las áreas.");

        Iterable<Area> iterable = areaRepositorio.findAll();

        List<Area> areas = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        List<AreaDTO> areaDTOs = areas.stream()
                .map(AreaDTO::new) // Convertir cada Area a un AreaDTO
                .collect(Collectors.toList());

        log.info("Consulta de todas las áreas realizada con éxito. Total de áreas: {}", areaDTOs.size());
        return areaDTOs;
    }



    public Area consultarArea(Long id) {
        log.info("Intentando consultar el área con ID: {}", id);
        Area area = areaRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al consultar. Área no encontrada con ID: {}", id);
                    return new CustomException(EnumCodigos.AREA_NO_ENCONTRADA);
                });
        log.info("Área consultada con éxito: {}", area);
        return area;
    }
}