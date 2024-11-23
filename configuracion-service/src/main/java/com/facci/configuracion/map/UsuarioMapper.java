package com.facci.configuracion.map;

import com.facci.configuracion.dominio.RolUsuario;
import com.facci.configuracion.dominio.Usuario;
import com.facci.configuracion.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioMapper {
    public UsuarioDTO mapToDto(Usuario valor) {
        return UsuarioDTO.builder()
                .id(valor.getId())
                .nombreUsuario(valor.getNombreUsuario())
                .nombreCompleto(valor.getNombreCompleto())
                .correo(valor.getCorreo())
                .activo(valor.isActivo())
                .roles(valor.getRoles().stream()
                        .map(RolUsuario::getRolUsuario)
                        .collect(Collectors.toList()))
                .build();
    }

    public Usuario mapToEntidad(UsuarioDTO valor) {
        return Usuario.builder()
                .nombreUsuario(valor.getNombreUsuario())
                .nombreCompleto(valor.getNombreCompleto())
                .correo(valor.getCorreo())
                .activo(valor.isActivo())
                .build();
    }

    public List<UsuarioDTO> map(List<Usuario> list) {
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }
}
