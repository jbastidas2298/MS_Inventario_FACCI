package com.facci.inventario.controlador;

import com.facci.inventario.dto.GrupoActivoDTO;
import com.facci.inventario.servicio.GrupoActivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/inventario/articulo/grupoActivo")
@Tag(name = "Grupo Activo", description = "Operaciones relacionadas con grupos activos")
public class GrupoActivoControlador {
    public final GrupoActivoService grupoActivoService;

    @PostMapping()
    @Operation(summary = "Guardar un nuevo grupo activo", description = "Guarda un nuevo grupo activo en el sistema")
    public ResponseEntity<?> guardarGrupo(@RequestBody GrupoActivoDTO grupoActivoDTO) {
        var respuesta = grupoActivoService.guardarGrupoActivo(grupoActivoDTO);
        return ResponseEntity.ok(Map.of(
                "respuesta", respuesta
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un grupo activo", description = "Elimina un grupo activo existente en el sistema")
    public ResponseEntity<?> eliminarGrupo(@PathVariable Long id) {
        var respuesta = grupoActivoService.eliminarGrupoActivo(id);
        return ResponseEntity.ok(Map.of(
                "respuesta", respuesta
        ));
    }

    @GetMapping()
    @Operation(summary = "Listar todos los grupos activos", description = "Obtiene una lista de todos los grupos activos registrados en el sistema")
    public ResponseEntity<List<GrupoActivoDTO>> listarGrupoActivos() {
        List<GrupoActivoDTO> grupos = grupoActivoService.listarGrupoActivos();
        return ResponseEntity.ok(grupos);
    }
}
