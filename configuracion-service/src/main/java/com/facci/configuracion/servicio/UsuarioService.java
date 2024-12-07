package com.facci.configuracion.servicio;

import com.facci.configuracion.dominio.RolUsuario;
import com.facci.configuracion.dominio.Usuario;
import com.facci.configuracion.dto.UsuarioAreaDTO;
import com.facci.configuracion.dto.UsuarioDTO;
import com.facci.configuracion.enums.EnumCodigos;
import com.facci.configuracion.enums.TipoRelacion;
import com.facci.configuracion.handler.CustomException;
import com.facci.configuracion.map.UsuarioMapper;
import com.facci.configuracion.repositorio.AreaRepositorio;
import com.facci.configuracion.repositorio.UsuarioRepositorio;
import com.facci.configuracion.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UsuarioService {
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private UsuarioMapper usuarioMapper;
    private final AreaRepositorio areaRepositorio;

    @Value("${spring.security.user.name}")
    private String adminUsername;


    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public UsuarioService(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper, AreaRepositorio areaRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.areaRepositorio = areaRepositorio;
    }

    public ResponseEntity<?> registrar(UsuarioDTO usuarioDTO) {
        var usuarioOp = this.usuarioRepositorio.findByNombreUsuario(usuarioDTO.getNombreUsuario());
        if (usuarioOp.isPresent()) {
            log.error("Ya se encuentra registrado el usuario: {}", usuarioDTO.getNombreUsuario());
            throw new CustomException(EnumCodigos.USUARIO_YA_EXISTE);

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
        var usuarioOp = this.usuarioRepositorio.findById(usuarioDto.getId());

        if (usuarioOp.isEmpty()) {
            String mensajeError = "Usuario no encontrado con id: " + usuarioDto.getId();
            log.error(mensajeError);
            throw new CustomException(EnumCodigos.USUARIO_NO_ENCONTRADO);
        }

        var usuarioRecargado = usuarioOp.get();

        try {
            usuarioRecargado.setActivo(usuarioDto.isActivo());
            usuarioRecargado.setCorreo(usuarioDto.getCorreo());
            usuarioRecargado.setNombreCompleto(usuarioDto.getNombreCompleto());
            usuarioRecargado.getRoles().clear();
            if(!usuarioDto.getContrasena().isEmpty()){
                String encryptedPassword = passwordEncoder.encode(usuarioDto.getContrasena());
                usuarioDto.setContrasena(encryptedPassword);
            }
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
            throw new CustomException(EnumCodigos.USUARIO_NO_ENCONTRADO);
        }
        try {
            usuarioRepositorio.delete(usuarioOp.get());
            log.info("Usuario eliminado con id: {}", id);
            ApiResponse response = new ApiResponse(EnumCodigos.USUARIO_ELIMINADO, null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String mensajeError = "Error al eliminar el usuario con id: " + id;
            log.error(mensajeError, e);
            throw new CustomException(EnumCodigos.ERROR_ELIMINAR_USUARIO);
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
            throw new CustomException(EnumCodigos.ERROR_CONSULTA_USUARIO);
        }
    }

    public ResponseEntity<UsuarioAreaDTO> consultarUsuarioArea(long id, TipoRelacion tipoRelacion) {
        UsuarioAreaDTO usuarioAreaDTO = new UsuarioAreaDTO();
        usuarioAreaDTO.setTipoRelacion(tipoRelacion);

        if (tipoRelacion == TipoRelacion.AREA){
            var area = this.areaRepositorio.findById(id);
            if (area.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            usuarioAreaDTO.setId(area.get().getId());
            usuarioAreaDTO.setNombre(area.get().getNombreArea());
        }else{
            var usuarioOp = this.usuarioRepositorio.findById(id);
            if (usuarioOp.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            usuarioAreaDTO.setId(usuarioOp.get().getId());
            usuarioAreaDTO.setNombre(usuarioOp.get().getNombreCompleto());
        }
        try {
            return ResponseEntity.ok(usuarioAreaDTO);
        } catch (Exception e) {
            String mensajeError = "Error al obtener el usuario con id: " + id;
            log.error(mensajeError, e);
            throw new CustomException(EnumCodigos.ERROR_CONSULTA_USUARIO);
        }
    }

    public ResponseEntity<List<UsuarioAreaDTO>> consultarUsuarioAreaTodos() {
        try {
            List<UsuarioAreaDTO> resultado = new ArrayList<>();

            this.areaRepositorio.findAll().forEach(area -> {
                UsuarioAreaDTO dto = new UsuarioAreaDTO();
                dto.setId(area.getId());
                dto.setNombre(area.getNombreArea());
                dto.setTipoRelacion(TipoRelacion.AREA);
                resultado.add(dto);
            });

            this.usuarioRepositorio.findAll().forEach(usuario -> {
                UsuarioAreaDTO dto = new UsuarioAreaDTO();
                dto.setId(usuario.getId());
                dto.setNombre(usuario.getNombreCompleto());
                dto.setTipoRelacion(TipoRelacion.USUARIO);
                resultado.add(dto);
            });

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            String mensajeError = "Error al obtener áreas y usuarios.";
            log.error(mensajeError, e);
            throw new CustomException(EnumCodigos.ERROR_CONSULTA_USUARIO);
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

    @Transactional(readOnly = true)
    public UsuarioDTO listarPorNombreUsuario(String nombreUsuario) {
        var usuarioOp = this.usuarioRepositorio.findByNombreUsuario(nombreUsuario);
        if (adminUsername.equalsIgnoreCase(nombreUsuario)) {
            return null;
        }
        if (usuarioOp.isEmpty()) {
            throw new CustomException(EnumCodigos.USUARIO_NO_ENCONTRADO);
        }
        var usuario = this.usuarioMapper.mapToDto(usuarioOp.get());
        return usuario;
    }
}
