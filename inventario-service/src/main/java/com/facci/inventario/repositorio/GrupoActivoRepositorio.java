package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.GrupoActivo;

import java.util.Optional;

public interface GrupoActivoRepositorio extends BaseRepositorio<GrupoActivo>{
    Optional<GrupoActivo> findByCodigo(String codigo);
    GrupoActivo findByDescripcion(String descripcion);
    Optional<GrupoActivo> findById(long id);
}
