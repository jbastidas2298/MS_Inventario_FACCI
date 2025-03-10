package com.facci.configuracion.controlador;

import com.facci.comun.dto.UsuarioAreaDTO;
import com.facci.comun.dto.UsuarioDTO;
import com.facci.comun.enums.TipoRelacion;
import com.facci.configuracion.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/configuraciones")
public class ConfiguracionControlador {

    private final UsuarioService usuarioService;

    public ConfiguracionControlador(UsuarioService usuarioService){

        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuario por ID", description = "Obtiene un usuario registrado en el sistema")
    public ResponseEntity<UsuarioDTO> consultarUsuario(@PathVariable Long id) {
        return usuarioService.consultarUsuario(id);
    }

    @GetMapping("/{id}/{tipoRelacion}")
    @Operation(summary = "Consultar usuario por ID", description = "Obtiene un usuario registrado en el sistema")
    public ResponseEntity<UsuarioAreaDTO> consultarUsuarioArea(@PathVariable Long id, @PathVariable TipoRelacion tipoRelacion) {
        return usuarioService.consultarUsuarioArea(id,tipoRelacion);
    }

    @GetMapping("/nombreUsuario/{nombreUsuario}")
    public ResponseEntity<UsuarioDTO> buscarPorNombreUsuario(@PathVariable("nombreUsuario") String nombreUsuario) {
        var usuario = usuarioService.listarPorNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("usuarioArea")
    @Operation(summary = "Consultar Usuario-Areas", description = "Obtiene un usuario registrado en el sistema")
    public ResponseEntity <List<UsuarioAreaDTO>>consultarUsuarioArea() {
        return usuarioService.consultarUsuarioAreaTodos();
    }
}
