package com.facci.configuracion.dto;

import com.facci.configuracion.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UsuarioDTO {
    private long id;

    private String nombreUsuario;

    private String contrasena;

    private String correo;

    private boolean activo;

    private String nombreCompleto;

    private RolUsuario rolUsuario;

    public UsuarioDTO(String nombreCompleto,String nombreUsuario, String correo, String contrasena, boolean estado,
                      RolUsuario rolUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.nombreCompleto = nombreCompleto;
        this.contrasena = contrasena;
        this.activo = estado;
        this.rolUsuario = rolUsuario;
    }

}
