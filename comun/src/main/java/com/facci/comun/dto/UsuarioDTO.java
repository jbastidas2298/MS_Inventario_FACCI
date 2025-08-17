package com.facci.comun.dto;

import com.facci.comun.enums.EnumRolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    private String identificacion;

    private List<EnumRolUsuario> roles = new ArrayList<>();

    public UsuarioDTO(String nombreCompleto, String nombreUsuario, String correo, String contrasena, boolean estado,
                      EnumRolUsuario enumRolUsuario, String identificacion) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.nombreCompleto = nombreCompleto;
        this.identificacion = identificacion;
        this.contrasena = contrasena;
        this.activo = estado;
        this.roles.add(enumRolUsuario);
    }
}