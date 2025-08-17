package com.facci.configuracion.dominio;

import com.facci.comun.dto.UsuarioDTO;
import jakarta.persistence.*;
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
    @Column(unique = true, nullable = false)
    private String nombreUsuario;

    private String contrasena;

    private String correo;

    private boolean activo;

    private String nombreCompleto;

    private String identificacion;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RolUsuario> roles = new ArrayList<>();

    public Usuario(String nombreCompleto,String nombreUsuario, String correo, String contrasena, String identificacion) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.nombreCompleto = nombreCompleto;
        this.identificacion = identificacion;
        this.contrasena = contrasena;
        this.activo = true;
    }

    public Usuario(UsuarioDTO usuarioDTO) {
        this.nombreUsuario = usuarioDTO.getNombreUsuario();
        this.correo = usuarioDTO.getCorreo();
        this.nombreCompleto = usuarioDTO.getNombreCompleto();
        this.contrasena = usuarioDTO.getContrasena();
        this.identificacion = usuarioDTO.getIdentificacion();
        this.activo = usuarioDTO.isActivo();
        this.roles = usuarioDTO.getRoles().stream()
                .map(rol -> new RolUsuario(rol, this))
                .collect(Collectors.toList());
    }
}
