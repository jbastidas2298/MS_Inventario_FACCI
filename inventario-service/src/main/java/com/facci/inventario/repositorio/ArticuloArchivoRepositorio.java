package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.ArticuloArchivo;

import java.util.Optional;

public interface ArticuloArchivoRepositorio extends BaseRepositorio<ArticuloArchivo>{

    Optional<ArticuloArchivo> findByPath(String path);
}
