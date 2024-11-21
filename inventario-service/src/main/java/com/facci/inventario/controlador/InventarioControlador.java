package com.facci.inventario.controlador;

import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.monitoreo.AuditorAwareHolder;
import com.facci.inventario.monitoreo.UtilidadesSeguridadReactiva;
import com.facci.inventario.servicio.ArticuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<?>> registrar(@RequestBody ArticuloDTO dto) {
        return UtilidadesSeguridadReactiva.nombreUsuarioEnSesion()
                .flatMap(user -> {
                    AuditorAwareHolder.setAuditor(user); // Establece el auditor
                    return articuloService.registrar(dto); // Llama al servicio de forma reactiva
                })
                .doFinally(signalType -> AuditorAwareHolder.clear()); // Limpia el auditor
    }


    @PutMapping
    @Operation(summary = "Actualizar un articulo existente", description = "Actualiza la información de un articulo ya registrado")
    public ResponseEntity<?> actualizar(@RequestBody ArticuloDTO dto) {
        return articuloService.actualizar(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un articulo existente", description = "Elimina un articulo ya registrado en el sistema")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
       return articuloService.eliminar(id);
    }

    @GetMapping
    @Operation(summary = "Consultar todos los articulos", description = "Obtiene una lista de todos los articulos registrados en el sistema")
    public ResponseEntity<List<ArticuloDTO>> consultarUsuario() {
        return articuloService.consultarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar articulo por ID", description = "Obtiene un articulo registrado en el sistema")
    public ResponseEntity<ArticuloDTO> consultarUsuario(@PathVariable Long id) {
        return articuloService.consultarArticulo(id);
    }
    @GetMapping("/usuario")
    public Mono<String> obtenerUsuarioActual() {
        return UtilidadesSeguridadReactiva.nombreUsuarioEnSesion();
    }


/*    @GetMapping("/nombreUsuario/{nombreUsuario}")
    public ResponseEntity<UsuarioDTO> buscarPorNombreUsuario(@PathVariable("nombreUsuario") String nombreUsuario) {
        var usuario = servicio.listarPorNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(usuario);
    }*/
}
