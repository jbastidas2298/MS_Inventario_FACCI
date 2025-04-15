package com.facci.configuracion.servicio;

import com.facci.comun.enums.EnumCodigos;
import com.facci.comun.handler.CustomException;
import com.facci.configuracion.dominio.ContrasenaTokenReseteo;
import com.facci.configuracion.dominio.Usuario;
import com.facci.configuracion.repositorio.ContrasenaTokenReseteoRepositorio;
import com.facci.configuracion.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContrasenaTokenReseteoServicio {
    private final UsuarioRepositorio usuarioRepositorio;
    private final ContrasenaTokenReseteoRepositorio tokenRepositorio;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean solicitarRestablecimiento(String email, String usuario) {
        Usuario user = usuarioRepositorio.findByCorreoAndNombreUsuario(email, usuario)
                .orElseThrow(() -> new CustomException(EnumCodigos.USUARIO_NO_ENCONTRADO));
        tokenRepositorio.findByUser(user).ifPresent(token -> {
            tokenRepositorio.deleteByToken(token.getToken());
        });

        ContrasenaTokenReseteo nuevoToken = new ContrasenaTokenReseteo();
        nuevoToken.setToken(UUID.randomUUID().toString());
        nuevoToken.setUser(user);
        nuevoToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        tokenRepositorio.save(nuevoToken);
        emailService.envioRestablecerClave(user, nuevoToken.getToken());
        return true;
    }

    @Transactional(readOnly = true)
    public boolean validarToken(String token) {
        Optional<ContrasenaTokenReseteo> tokenEntity = tokenRepositorio.findByToken(token);
        if (tokenEntity.isEmpty()) {
            log.info("Token no encontrado: {}", token);
            throw new CustomException(EnumCodigos.ERROR_TOKEN);
        }
        if (tokenEntity.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            log.info("Token expirado: {}", token);
            throw new CustomException(EnumCodigos.ERROR_TOKEN);
        }
        return true;
    }

    @Transactional
    public boolean actualizarContraseña(String token, String nuevaContrasena) {
        try {
            Optional<ContrasenaTokenReseteo> tokenEntity = tokenRepositorio.findByToken(token);
            if (tokenEntity.isEmpty()) {
                log.info("Token no encontrado: {}", token);
                throw new CustomException(EnumCodigos.ERROR_TOKEN);
            }
            if (tokenEntity.get().getExpiryDate().isBefore(LocalDateTime.now())) {
                log.info("Token expirado: {}", token);
                throw new CustomException(EnumCodigos.ERROR_TOKEN);
            }
            Usuario user = tokenEntity.get().getUser();
            user.setContrasena(passwordEncoder.encode(nuevaContrasena));
            usuarioRepositorio.save(user);
            tokenRepositorio.deleteByToken(token);
            return true;
        } catch (CustomException e) {
            log.error("Error al validar el token: {}", e.getMessage());
            throw e;
        }
    }
}
