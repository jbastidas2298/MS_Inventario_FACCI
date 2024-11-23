package com.facci.inventario.controlador;

import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.servicio.ArticuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventario/articulo/items")
@Tag(name = "Articulo", description = "Operaciones relacionadas con articulos")
public class InventarioControlador {

    private final ArticuloService articuloService;

    public InventarioControlador(ArticuloService articuloService) {
        this.articuloService = articuloService;
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
    @Operation(summary = "Consultar todos los artículos", description = "Obtiene una lista de todos los artículos registrados en el sistema")
    public List<ArticuloDTO> consultarTodos() {
        return articuloService.consultarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar artículo por ID", description = "Obtiene un artículo registrado en el sistema")
    public ResponseEntity<ArticuloDTO> consultarArticulo(@PathVariable Long id) {
        ArticuloDTO articulo = articuloService.consultarArticulo(id);
        return ResponseEntity.ok(articulo);
    }
}
