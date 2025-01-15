package com.facci.configuracion.dominio;

import com.facci.comun.enums.EnumRolUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RolUsuario extends EntidadBase{

    @Enumerated(EnumType.STRING)
    private EnumRolUsuario rolUsuario; // Enum de roles

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
