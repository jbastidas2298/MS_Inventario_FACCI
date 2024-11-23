package com.facci.configuracion.dominio;

import com.facci.configuracion.dto.UsuarioDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends EntidadBase {

    private String nombreUsuario;

    private String contrasena;

    private String correo;

    private boolean activo;

    private String nombreCompleto;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RolUsuario> roles = new ArrayList<>();

    public Usuario(String nombreCompleto,String nombreUsuario, String correo, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.nombreCompleto = nombreCompleto;
        this.contrasena = contrasena;
        this.activo = true;
    }

    public Usuario(UsuarioDTO usuarioDTO) {
        this.nombreUsuario = usuarioDTO.getNombreUsuario();
        this.correo = usuarioDTO.getCorreo();
        this.nombreCompleto = usuarioDTO.getNombreCompleto();
        this.contrasena = usuarioDTO.getContrasena();
        this.activo = usuarioDTO.isActivo();
        this.roles = usuarioDTO.getRoles().stream()
                .map(rol -> new RolUsuario(rol, this))
                .collect(Collectors.toList());
    }
}
