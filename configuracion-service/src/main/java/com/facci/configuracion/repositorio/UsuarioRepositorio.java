package com.facci.configuracion.repositorio;

import com.facci.configuracion.dominio.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends BaseRepositorio<Usuario> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    Page<Usuario> findByNombreCompletoContainingIgnoreCaseOrNombreUsuarioContainingIgnoreCase(
            String nombre, String codigoInterno, Pageable pageable);
}
