package com.facci.configuracion.repositorio;

import com.facci.configuracion.dominio.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends BaseRepositorio<Usuario> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    List<Usuario> findByNombreUsuarioOrIdentificacion(String nombreUsuario, String identificacion);
    Page<Usuario> findByNombreCompletoContainingIgnoreCaseOrNombreUsuarioContainingIgnoreCase(
            String nombre, String codigoInterno, Pageable pageable);
    Optional<Usuario> findByCorreoAndNombreUsuario(String correo, String nombreUsuario);
    Optional<Usuario> findByIdentificacion(String identificacionUsuario);
}
