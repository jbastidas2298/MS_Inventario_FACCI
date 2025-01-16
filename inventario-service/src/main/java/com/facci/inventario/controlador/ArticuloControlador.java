package com.facci.inventario.controlador;

import com.facci.comun.enums.EnumCodigos;
import com.facci.comun.enums.TipoRelacion;
import com.facci.comun.response.ApiResponse;
import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.dto.ArticuloAsignacionDTO;
import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.dto.ArticuloDetalleDTO;
import com.facci.inventario.servicio.ArchivoService;
import com.facci.inventario.servicio.ArticuloAsignacionService;
import com.facci.inventario.servicio.ArticuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/inventario/articulo/items")
@Tag(name = "Articulo", description = "Operaciones relacionadas con articulos")
public class ArticuloControlador {

    private final ArticuloService articuloService;
    private final ArchivoService archivoService;
    private final ArticuloAsignacionService articuloAsignacionService;

    public ArticuloControlador(ArticuloService articuloService, ArchivoService archivoService, ArticuloAsignacionService articuloAsignacionService) {
        this.articuloService = articuloService;
        this.archivoService = archivoService;
        this.articuloAsignacionService = articuloAsignacionService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo artículo", description = "Registra un nuevo artículo en el sistema")
    public ResponseEntity<ArticuloDTO> registrar(@RequestBody ArticuloDTO dto) {
        ArticuloDTO articuloRegistrado = articuloService.registrar(dto);
        return ResponseEntity.ok(articuloRegistrado);
    }

    @PutMapping
    @Operation(summary = "Actualizar un artículo existente", description = "Actualiza la información de un artículo ya registrado")
    public ResponseEntity<ArticuloDTO> actualizar(@RequestBody ArticuloDTO dto) {
        ArticuloDTO articuloActualizado = articuloService.actualizar(dto);
        return ResponseEntity.ok(articuloActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un artículo existente", description = "Elimina un artículo ya registrado en el sistema")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        articuloService.eliminar(id);
        return ResponseEntity.ok("Artículo eliminado exitosamente.");
    }

    @GetMapping
    @Operation(summary = "Consultar todos los artículos", description = "Obtiene una lista de todos los artículos registrados en el sistema con paginación y filtrado")
    public Page<ArticuloDTO> consultarTodos(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size,
            @RequestParam Optional<String> filter
    ) {
        return articuloService.consultarTodos(page, size, filter);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Consultar artículo por ID", description = "Obtiene un artículo registrado en el sistema mediante su ID")
    public ResponseEntity<ArticuloDTO> consultarArticulo(@PathVariable Long id) {
        ArticuloDTO articulo = articuloService.consultarArticulo(id);
        return ResponseEntity.ok(articulo);
    }


    @PostMapping("/asignar")
    @Operation(summary = "Asignar artículos", description = "Permite asignar múltiples artículos a una relación específica")
    public ResponseEntity<List<ArticuloAsignacionDTO>> asignarArticulos(
            @RequestParam Long idRelacionado,
            @RequestParam TipoRelacion tipoRelacion,
            @RequestBody List<Long> idsArticulos) {
        List<ArticuloAsignacionDTO> asignaciones = articuloAsignacionService.asignarArticulos(idRelacionado, tipoRelacion, idsArticulos);
        return ResponseEntity.ok(asignaciones);
    }

    @PostMapping("/reasignar-todos")
    @Operation(summary = "Reasignar todos los artículos", description = "Permite reasignar todos los artículos de un usuario a otro con una nueva relación")
    public ResponseEntity<Map<String, Object>> reasignarArticulos(
            @RequestParam Long idUsuarioActual,
            @RequestParam TipoRelacion tipoRelacionActual,
            @RequestParam Long idUsuarioNuevo,
            @RequestParam TipoRelacion tipoRelacionNuevo,
            @RequestParam String descripcion) {
        List<ArticuloAsignacion> reasignaciones = articuloAsignacionService.reasignarArticulosTodos(
                idUsuarioActual, tipoRelacionActual, idUsuarioNuevo, tipoRelacionNuevo, descripcion);
        return ResponseEntity.ok(
                ApiResponse.buildResponse(
                        EnumCodigos.ARCHIVO_SUBIDO_EXITO
                )
        );
    }

    @PostMapping("/reasignar-articulos")
    @Operation(summary = "Reasignar artículos específicos", description = "Permite reasignar una lista específica de artículos a un nuevo usuario")
    public ResponseEntity<List<ArticuloAsignacion>> reasignarArticulos(
            @RequestBody List<Long> idsArticulos,
            @RequestParam Long idUsuarioNuevo,
            @RequestParam String descripcion) {
        List<ArticuloAsignacion> reasignaciones = articuloAsignacionService.reasignarArticulos(idsArticulos, idUsuarioNuevo, descripcion);
        return ResponseEntity.ok(reasignaciones);
    }


    @GetMapping("/articuloDetalle/{id}")
    @Operation(summary = "Consultar detalles de un artículo", description = "Obtiene todos los detalles de un artículo mediante su ID")
    public ArticuloDetalleDTO consultarArticuloDetalle(@PathVariable Long id) {
        return articuloService.obtenerArticuloDetalle(id);
    }

    @GetMapping("/asignaciones")
    @Operation(summary = "Consultar asignaciones", description = "Obtiene una lista detallada de todas las asignaciones de artículos")
    public Page<ArticuloAsignacionDTO> consultarAsignaciones(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size,
            @RequestParam Optional<String> filter) {
        return articuloAsignacionService.obtenerAsignacionesConDetalles(page, size, filter);
    }

}
