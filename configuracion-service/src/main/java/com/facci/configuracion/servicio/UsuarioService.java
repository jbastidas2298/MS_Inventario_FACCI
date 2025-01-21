package com.facci.configuracion.servicio;

import com.facci.comun.dto.UsuarioAreaDTO;
import com.facci.comun.dto.UsuarioDTO;
import com.facci.comun.enums.EnumCodigos;
import com.facci.comun.enums.EnumRolUsuario;
import com.facci.comun.enums.TipoRelacion;
import com.facci.comun.handler.CustomException;
import com.facci.comun.response.ApiResponse;
import com.facci.configuracion.dominio.RolUsuario;
import com.facci.configuracion.dominio.Usuario;
import com.facci.configuracion.map.UsuarioMapper;
import com.facci.configuracion.repositorio.AreaRepositorio;
import com.facci.configuracion.repositorio.UsuarioRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UsuarioService {
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private UsuarioMapper usuarioMapper;
    private final AreaRepositorio areaRepositorio;
    private final EmailService emailService;

    @Value("${spring.security.user.name}")
    private String adminUsername;


    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public UsuarioService(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper, AreaRepositorio areaRepositorio, EmailService emailService) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.areaRepositorio = areaRepositorio;
        this.emailService = emailService;
    }

    public ResponseEntity<?> registrar(UsuarioDTO usuarioDTO) {
        String contraseña = usuarioDTO.getContrasena();
        var usuarioOp = this.usuarioRepositorio.findByNombreUsuario(usuarioDTO.getNombreUsuario());
        if (usuarioOp.isPresent()) {
            log.error("Ya se encuentra registrado el usuario: {}", usuarioDTO.getNombreUsuario());
            throw new CustomException(EnumCodigos.USUARIO_YA_EXISTE);

        }
        String encryptedPassword = passwordEncoder.encode(usuarioDTO.getContrasena());
        usuarioDTO.setContrasena(encryptedPassword);
        Usuario nuevoUsuario = new Usuario(usuarioDTO);
        Usuario usuarioGuardado = usuarioRepositorio.save(nuevoUsuario);
        emailService.enviarCorreo(usuarioDTO, contraseña);
        log.debug("Usuario registrado: {}", usuarioDTO.getNombreUsuario());
        return ResponseEntity.ok(this.usuarioMapper.mapToDto(usuarioGuardado));
    }

    @Transactional
    public ResponseEntity<?> actualizar(UsuarioDTO usuarioDto) {
        String contraseña = usuarioDto.getContrasena();
        var usuarioOp = this.usuarioRepositorio.findById(usuarioDto.getId());
        if (usuarioOp.isEmpty()) {
            String mensajeError = "Usuario no encontrado con id: " + usuarioDto.getId();
            log.error(mensajeError);
            throw new CustomException(EnumCodigos.USUARIO_NO_ENCONTRADO);
        }
        var usuarioRecargado = usuarioOp.get();
        try {
            usuarioRecargado.setNombreUsuario(usuarioDto.getNombreUsuario());
            usuarioRecargado.setActivo(usuarioDto.isActivo());
            usuarioRecargado.setCorreo(usuarioDto.getCorreo());
            usuarioRecargado.setNombreCompleto(usuarioDto.getNombreCompleto());
            if(!usuarioDto.getContrasena().isEmpty()){
                String encryptedPassword = passwordEncoder.encode(usuarioDto.getContrasena());
                usuarioRecargado.setContrasena(encryptedPassword);
            }
            usuarioRecargado.getRoles().clear();
            List<RolUsuario> nuevosRoles = usuarioDto.getRoles().stream()
                    .map(rolEnum -> new RolUsuario(rolEnum, usuarioRecargado))
                    .collect(Collectors.toList());
            usuarioRecargado.getRoles().addAll(nuevosRoles);

            Usuario usuarioActualizado = usuarioRepositorio.save(usuarioRecargado);
            emailService.enviarCorreo(usuarioDto, contraseña);
            log.info("Usuario modificado: {}", usuarioActualizado.getNombreUsuario());
            return ResponseEntity.ok(this.usuarioMapper.mapToDto(usuarioActualizado));
        } catch (Exception e) {
            String mensajeError = "Error al actualizar el usuario: " + usuarioRecargado.getNombreUsuario();
            log.error(mensajeError, e);
            throw  new CustomException(EnumCodigos.ERROR_ACTUALIZAR_USUARIO);
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

    public Page<UsuarioDTO> consultarTodosPaginados(Optional<Integer> page, Optional<Integer> size, Optional<String> filter) {
        try {
            Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
            String filterValue = filter.orElse("").trim();
            Page<Usuario> usuariosPaginados = usuarioRepositorio.findByNombreCompletoContainingIgnoreCaseOrNombreUsuarioContainingIgnoreCase(
                    filterValue, filterValue, pageable);
            List<UsuarioDTO> usuariosDto = usuariosPaginados.getContent().stream()
                    .map(usuarioMapper::mapToDto)
                    .collect(Collectors.toList());
            return new PageImpl<>(usuariosDto, pageable, usuariosPaginados.getTotalElements());
        }catch (Exception e){
            log.error("Error en consulta de usuarios",e.getCause());
            throw new CustomException(EnumCodigos.ERROR_CONSULTA_USUARIO);
        }
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

    @Transactional
    public List<UsuarioDTO> procesarExcel(MultipartFile file) throws Exception {
        try {
            List<UsuarioDTO> usuarioDTOS = new ArrayList<>();
            if (file.isEmpty() || !file.getOriginalFilename().endsWith(".xlsx")) {
                throw new IllegalArgumentException("El archivo no es un Excel válido.");
            }
            try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    try {
                        String nombreCompleto = row.getCell(0).getStringCellValue();
                        if (nombreCompleto == null || nombreCompleto.isEmpty()) continue;
                        String correo = row.getCell(1).getStringCellValue();
                        String nombreUsuario = generarNombreUsuario(nombreCompleto);
                        String contrasena = generarContrasena();
                        UsuarioDTO usuarioDTO = new UsuarioDTO(
                                nombreCompleto,
                                nombreUsuario,
                                correo,
                                contrasena,
                                false,
                                EnumRolUsuario.DOCENTE
                        );
                        registrarUsuario(usuarioDTO);
                        usuarioDTOS.add(usuarioDTO);
                    } catch (CustomException e) {
                        log.error("Error procesando la fila {}: {}", i, e.getMessage());
                        throw e;
                    }
                }
            }
            return usuarioDTOS;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al procesar archivo Excel", e);
            throw new CustomException(EnumCodigos.ERROR_IMPORTAR_USUARIOS);
        }
    }

    private String generarNombreUsuario(String nombreCompleto) {
        try{
            String[] nombres = nombreCompleto.split(" ");

            String primerNombre = nombres[0].toLowerCase();
            String primerApellido = nombres[2].toLowerCase();

            String nombreUsuario = "";
            String baseUsuario = "";
            for (int i = 1; i <= primerNombre.length(); i++) {
                String letrasNombre = primerNombre.substring(0, i);
                baseUsuario = letrasNombre + primerApellido;

                if (!usuarioRepositorio.findByNombreUsuario(baseUsuario).isPresent()) {
                    return baseUsuario;
                }
            }
            int contador = 1;
            nombreUsuario = baseUsuario;
            while (usuarioRepositorio.findByNombreUsuario(nombreUsuario).isPresent()) {
                nombreUsuario = baseUsuario + contador;
                contador++;
            }

            return nombreUsuario;
        }catch (Exception e){
            log.error("Error al generar nombre de usuario:", nombreCompleto);
            throw new CustomException(EnumCodigos.ERROR_GENERAR_NOMBRE_USUARIO);
        }
    }


    private String generarContrasena() {
        int longitud = 8;
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    private void registrarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepositorio.findByNombreUsuario(usuarioDTO.getNombreUsuario()).isPresent()) {
            throw new RuntimeException("El usuario ya existe: " + usuarioDTO.getNombreUsuario());
        }

        String encryptedPassword = passwordEncoder.encode(usuarioDTO.getContrasena());
        usuarioDTO.setContrasena(encryptedPassword);

        Usuario nuevoUsuario = new Usuario(usuarioDTO);
        usuarioRepositorio.save(nuevoUsuario);
    }
}
