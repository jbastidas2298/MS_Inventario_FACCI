package com.facci.configuracion.repositorio;

import com.facci.configuracion.dominio.ContrasenaTokenReseteo;
import com.facci.configuracion.dominio.Usuario;

import java.util.Optional;

public interface ContrasenaTokenReseteoRepositorio extends BaseRepositorio<ContrasenaTokenReseteo> {
    Optional<ContrasenaTokenReseteo> findByToken(String token);
    void deleteByToken(String token);
    Optional<ContrasenaTokenReseteo> findByUser(Usuario user);
}
