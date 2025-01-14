package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.Articulo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArticuloRepositorio extends BaseRepositorio<Articulo>{
    Optional<Articulo>findByCodigoOrigen(String codigoOrigen);

    Page<Articulo> findByNombreContainingIgnoreCaseOrCodigoOrigenContainingIgnoreCase(
            String nombre, String codigoInterno, Pageable pageable);

    Page<Articulo> findByIdInAndNombreContainingIgnoreCaseOrCodigoOrigenContainingIgnoreCase(
            List<Long> ids, String nombre, String codigoInterno, Pageable pageable);
}
