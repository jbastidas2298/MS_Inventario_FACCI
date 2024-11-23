package com.facci.configuracion.servicio;

import com.facci.configuracion.dominio.RolUsuario;
import com.facci.configuracion.dominio.Usuario;
import com.facci.configuracion.dto.UsuarioDTO;
import com.facci.configuracion.enums.EnumErrores;
import com.facci.configuracion.handler.CustomException;
import com.facci.configuracion.map.UsuarioMapper;
import com.facci.configuracion.repositorio.UsuarioRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UsuarioService {
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private UsuarioMapper usuarioMapper;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public UsuarioService(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    public ResponseEntity<?> registrar(UsuarioDTO usuarioDTO) {
        var usuarioOp = this.usuarioRepositorio.findByNombreUsuario(usuarioDTO.getNombreUsuario());
        if (usuarioOp.isPresent()) {
            log.error("Ya se encuentra registrado el usuario: {}", usuarioDTO.getNombreUsuario());
            throw new CustomException(EnumErrores.USUARIO_YA_EXISTE);

        }
        String encryptedPassword = passwordEncoder.encode(usuarioDTO.getContrasena());
        usuarioDTO.setContrasena(encryptedPassword);
        Usuario nuevoUsuario = new Usuario(usuarioDTO);
        Usuario usuarioGuardado = usuarioRepositorio.save(nuevoUsuario);
        log.debug("Usuario registrado: {}", usuarioDTO.getNombreUsuario());
        return ResponseEntity.ok(this.usuarioMapper.mapToDto(usuarioGuardado));
    }

    @Transactional
    public ResponseEntity<?> actualizar(UsuarioDTO usuarioDto) {
        // Buscar el usuario en la base de datos
        var usuarioOp = this.usuarioRepositorio.findById(usuarioDto.getId());

        if (usuarioOp.isEmpty()) {
            String mensajeError = "Usuario no encontrado con id: " + usuarioDto.getId();
            log.error(mensajeError);
            throw new CustomException(EnumErrores.USUARIO_NO_ENCONTRADO);
        }

        var usuarioRecargado = usuarioOp.get();

        try {
            // Actualizar datos básicos del usuario
            usuarioRecargado.setActivo(usuarioDto.isActivo());
            usuarioRecargado.setCorreo(usuarioDto.getCorreo());
            usuarioRecargado.setNombreCompleto(usuarioDto.getNombreCompleto());

            usuarioRecargado.getRoles().clear();
            List<RolUsuario> nuevosRoles = usuarioDto.getRoles().stream()
                    .map(rolEnum -> new RolUsuario(rolEnum, usuarioRecargado))
                    .collect(Collectors.toList());
            usuarioRecargado.getRoles().addAll(nuevosRoles);
            Usuario usuarioActualizado = usuarioRepositorio.save(usuarioRecargado);

            log.info("Usuario modificado: {}", usuarioActualizado.getNombreUsuario());
            return ResponseEntity.ok(this.usuarioMapper.mapToDto(usuarioActualizado));
        } catch (Exception e) {
            String mensajeError = "Error al actualizar el usuario: " + usuarioRecargado.getNombreUsuario();
            log.error(mensajeError, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajeError);
        }
    }


    @Transactional
    public ResponseEntity<?> eliminar(Long id) {
        var usuarioOp = this.usuarioRepositorio.findById(id);

        if (usuarioOp.isEmpty()) {
            String mensajeError = "Usuario no encontrado con id: " + id;
            log.error(mensajeError);
            throw new CustomException(EnumErrores.USUARIO_NO_ENCONTRADO);
        }
        try {
            usuarioRepositorio.delete(usuarioOp.get());
            log.info("Usuario eliminado con id: {}", id);
            return ResponseEntity.ok("Usuario eliminado exitosamente.");
        } catch (Exception e) {
            String mensajeError = "Error al eliminar el usuario con id: " + id;
            log.error(mensajeError, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajeError);
        }
    }

    public ResponseEntity<UsuarioDTO> consultarUsuario(long id) {
        var usuarioOp = this.usuarioRepositorio.findById(id);

        if (usuarioOp.isEmpty()) {
            String mensajeError = "Usuario no encontrado con id: " + id;
            log.error(mensajeError);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            UsuarioDTO usuarioDto = this.usuarioMapper.mapToDto(usuarioOp.get());
            return ResponseEntity.ok(usuarioDto);
        } catch (Exception e) {
            String mensajeError = "Error al obtener el usuario con id: " + id;
            log.error(mensajeError, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<UsuarioDTO>> consultarTodos() {
        List<Usuario> usuarios = StreamSupport
                .stream(usuarioRepositorio.findAll().spliterator(), false)
                .collect(Collectors.toList());

        if (usuarios.isEmpty()) {
            log.info("No se encontraron usuarios registrados.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        List<UsuarioDTO> usuariosDto = usuarios.stream()
                .map(usuarioMapper::mapToDto)
                .collect(Collectors.toList());

        log.info("Usuarios consultados: {}", usuariosDto.size());
        return ResponseEntity.ok(usuariosDto);
    }

}
