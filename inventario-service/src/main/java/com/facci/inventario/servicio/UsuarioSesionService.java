package com.facci.inventario.servicio;

import com.facci.comun.dto.UsuarioDTO;
import com.facci.comun.enums.EnumCodigos;
import com.facci.comun.enums.EnumRolUsuario;
import com.facci.comun.handler.CustomException;
import com.facci.inventario.Configuracion.ConfiguracionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsuarioSesionService {

    private final ConfiguracionService configuracionService;

    public UsuarioSesionService(ConfiguracionService configuracionService){

        this.configuracionService = configuracionService;
    }

    public Optional<String> obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }
        return Optional.empty();
    }

    public UsuarioDTO usuarioCompleto(){
        UsuarioDTO usuarioSesion = new UsuarioDTO();
        String usuario = obtenerUsuarioActual()
                .orElseThrow(() -> new CustomException(EnumCodigos.USUARIO_ASIGNAR_EN_SESION));
        if(!usuario.equals("administrador")){
            usuarioSesion = configuracionService.buscarPorNombreUsuario(usuario);
        }else{
            usuarioSesion.setNombreUsuario("Administrador");
            usuarioSesion.setActivo(true);
            usuarioSesion.setNombreCompleto("Administrador");
        }
        if (usuarioSesion == null) {
            log.error("No se encontró información del usuario en el servicio de configuración para '{}'.", usuario);
            throw new CustomException(EnumCodigos.USUARIO_ASIGNAR_EN_SESION);
        }
        return usuarioSesion;
    }

    public List<EnumRolUsuario> obtenerRolesActuales() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> {
                    try {
                        return EnumRolUsuario.valueOf(role);
                    } catch (IllegalArgumentException e) {
                        log.warn("Rol desconocido: {}", role);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }




}
