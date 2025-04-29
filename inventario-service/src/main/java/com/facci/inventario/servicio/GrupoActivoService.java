package com.facci.inventario.servicio;

import com.facci.inventario.dominio.GrupoActivo;
import com.facci.inventario.dto.GrupoActivoDTO;
import com.facci.inventario.repositorio.GrupoActivoRepositorio;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GrupoActivoService {
    public final GrupoActivoRepositorio grupoActivoRepositorio;

    public boolean guardarGrupoActivo(GrupoActivoDTO grupoActivoDto) {
        log.info("Guardando grupo activo");
        try {
            GrupoActivo grupoActivo;

            if (grupoActivoDto.getId() > 0) {
                log.info("Actualizando grupo activo con ID: {}", grupoActivoDto.getId());
                grupoActivo = grupoActivoRepositorio.findById(grupoActivoDto.getId())
                        .orElseThrow(() -> new RuntimeException("Grupo activo no encontrado con id: " + grupoActivoDto.getId()));
            } else {
                log.info("Creando nuevo grupo activo");
                grupoActivo = new GrupoActivo();
            }
            grupoActivo.setCodigo(grupoActivoDto.getCodigo());
            grupoActivo.setDescripcion(grupoActivoDto.getDescripcion());
            grupoActivoRepositorio.save(grupoActivo);

            return true;
        } catch (Exception e) {
            log.error("Error al guardar el grupo activo: {}", e.getMessage());
            return false;
        }
    }


    public boolean eliminarGrupoActivo(Long id) {
        log.info("Eliminando grupo activo con id: {}", id);
        try {
            grupoActivoRepositorio.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Error al eliminar el grupo activo: {}", e.getMessage());
            return false;
        }
    }

    public List<GrupoActivoDTO> listarGrupoActivos() {
        log.info("Consultando todos los grupos activos");
        var grupoActivos = grupoActivoRepositorio.findAll();
        List<GrupoActivoDTO> lista = new ArrayList<>();
        grupoActivos.forEach(grupoActivo -> {
            GrupoActivoDTO grupoActivoDto = new GrupoActivoDTO();
            grupoActivoDto.setId(grupoActivo.getId());
            grupoActivoDto.setCodigo(grupoActivo.getCodigo());
            grupoActivoDto.setDescripcion(grupoActivo.getDescripcion());
            lista.add(grupoActivoDto);
        });
        return lista;
    }

}
