package com.facci.configuracion.controlador;

import com.facci.configuracion.servicio.ContrasenaTokenReseteoServicio;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/configuracion/restablecer-contrasena")
@Tag(name = "Restablecer Contraseña", description = "Restabelecimiento de contraseña")
public class ContrasenaTokenReseteoControlador {
    private final ContrasenaTokenReseteoServicio contrasenaTokenReseteoServicio;

    public ContrasenaTokenReseteoControlador(ContrasenaTokenReseteoServicio contrasenaTokenReseteoServicio) {
        this.contrasenaTokenReseteoServicio = contrasenaTokenReseteoServicio;
    }

    @PostMapping("/validar-token")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        var resetToken = contrasenaTokenReseteoServicio.validarToken(token);

        return ResponseEntity.ok(Map.of(
                "respuesta", resetToken
        ));
    }

    @PostMapping("/nueva-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String nuevaClave) {
        var resetToken = contrasenaTokenReseteoServicio.actualizarContraseña(token, nuevaClave);
        return ResponseEntity.ok(Map.of(
                "respuesta", resetToken
        ));
    }

    @PostMapping()
    public ResponseEntity<Map<String, Object>> restablecerClave(@RequestParam String email,
                                                                @RequestParam String usuario) {
        var exito = contrasenaTokenReseteoServicio.solicitarRestablecimiento(email,usuario);
        return ResponseEntity.ok(Map.of(
                "respuesta", exito
        ));
    }
}

