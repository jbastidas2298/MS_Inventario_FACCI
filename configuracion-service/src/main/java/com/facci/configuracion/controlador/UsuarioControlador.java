package com.facci.configuracion.controlador;

import com.facci.configuracion.dto.UsuarioDTO;
import com.facci.configuracion.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping
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
}
