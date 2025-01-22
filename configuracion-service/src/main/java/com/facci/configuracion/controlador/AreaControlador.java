package com.facci.configuracion.controlador;

import com.facci.configuracion.dominio.Area;
import com.facci.configuracion.dto.AreaDTO;
import com.facci.configuracion.servicio.AreaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/configuracion/areas")
@Tag(name = "Áreas", description = "Operaciones relacionadas con áreas")
public class AreaControlador {

    private final AreaService areaService;

    public AreaControlador(AreaService areaService) {
        this.areaService = areaService;
    }

    @PostMapping
    @Operation(summary = "Registrar un área", description = "Registra una nueva área en el sistema")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody AreaDTO areaDTO) {
        Map<String, Object> nuevaArea = areaService.registrar(areaDTO);
        return ResponseEntity.ok(nuevaArea);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un área existente", description = "Actualiza la información de un área ya registrada")
    public ResponseEntity<Map<String, Object>> actualizar(@RequestBody AreaDTO areaDTO) {
        Area areaActualizada = areaService.actualizar(areaDTO);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", areaActualizada.getId());
        respuesta.put("nombreArea", areaActualizada.getNombreArea());
        respuesta.put("nombreUsuario", areaActualizada.getUsuarioEncargado().getNombreCompleto());

        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un área existente", description = "Elimina un área ya registrada en el sistema")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        areaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Consultar todas las áreas", description = "Obtiene una lista de todas las áreas registradas en el sistema")
    public ResponseEntity<List<AreaDTO>> consultarAreas() {
        List<AreaDTO> areaDTOs = areaService.consultarTodas();
        return ResponseEntity.ok(areaDTOs);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Consultar área por ID", description = "Obtiene un área registrada en el sistema")
    public ResponseEntity<AreaDTO> consultarArea(@PathVariable Long id) {
        var area = areaService.consultarArea(id);
        return ResponseEntity.ok(area);
    }

}
