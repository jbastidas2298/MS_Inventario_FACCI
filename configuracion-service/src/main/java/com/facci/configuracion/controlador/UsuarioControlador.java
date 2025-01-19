package com.facci.configuracion.controlador;

import com.facci.comun.dto.UsuarioAreaDTO;
import com.facci.comun.dto.UsuarioDTO;
import com.facci.comun.handler.CustomException;
import com.facci.configuracion.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/configuracion/usuarios")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UsuarioControlador {

    private final UsuarioService usuarioService;

    public UsuarioControlador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDTO dto) {
        return usuarioService.registrar(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza la información de un usuario ya registrado")
    public ResponseEntity<?> actualizar(@RequestBody UsuarioDTO dto) {
        return usuarioService.actualizar(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario existente", description = "Elimina un usuario ya registrado en el sistema")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return usuarioService.eliminar(id);
    }

    @GetMapping
    @Operation(summary = "Consultar todos los usuarios", description = "Obtiene una lista de todos los usuarios registrados en el sistema")
    public ResponseEntity<List<UsuarioDTO>> consultarUsuario() {
        return usuarioService.consultarTodos();
    }

    @GetMapping("usuariosPaginados")
    @Operation(summary = "Consultar todos los usuarios", description = "Obtiene una lista de todos los usuarios registrados en el sistema")
    public Page<UsuarioDTO> consultarUsuario(
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size,
            @RequestParam Optional<String> filter) {
        return usuarioService.consultarTodosPaginados(page, size, filter);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuario por ID", description = "Obtiene un usuario registrado en el sistema")
    public ResponseEntity<UsuarioDTO> consultarUsuario(@PathVariable Long id) {
        return usuarioService.consultarUsuario(id);
    }

    @GetMapping("usuarioCompleto/{nombreUsuario}")
    public ResponseEntity<UsuarioDTO> buscarPorNombreUsuario(@PathVariable("nombreUsuario") String nombreUsuario) {
        var usuario = usuarioService.listarPorNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("usuarioArea")
    @Operation(summary = "Consultar Usuario-Areas", description = "Obtiene un usuario registrado en el sistema")
    public ResponseEntity <List<UsuarioAreaDTO>>consultarUsuarioArea() {
        return usuarioService.consultarUsuarioAreaTodos();
    }

    @PostMapping("/importar-excel")
    @Operation(summary = "Usuario Excel", description = "Importa masivamente usuario desde formato Excel")
    public ResponseEntity<?> importarExcel(@RequestParam("file") MultipartFile file) {
        try {
            var usuariosProcesados = usuarioService.procesarExcel(file);
            return ResponseEntity.ok(usuariosProcesados);
        } catch (CustomException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("codigo", e.getCodigo());
            errorResponse.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("codigo", "ERR000", "mensaje", "Error inesperado en el servidor"));
        }
    }

}
