package com.facci.configuracion.repositorio;

import com.facci.configuracion.dominio.Usuario;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends BaseRepositorio<Usuario> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

}
