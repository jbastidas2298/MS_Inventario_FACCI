package com.facci.configuracion.dominio;

import com.facci.configuracion.dto.UsuarioDTO;
import com.facci.configuracion.enums.RolUsuario;
import jakarta.persistence.Entity;
import lombok.*;

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

    private RolUsuario rolUsuario;

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
        this.rolUsuario = usuarioDTO.getRolUsuario();
    }
}
