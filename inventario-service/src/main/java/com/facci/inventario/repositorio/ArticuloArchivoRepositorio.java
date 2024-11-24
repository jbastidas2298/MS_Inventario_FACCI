package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.ArticuloArchivo;
import com.facci.inventario.enums.TipoArchivo;

import java.util.List;
import java.util.Optional;

public interface ArticuloArchivoRepositorio extends BaseRepositorio<ArticuloArchivo>{

    Optional<ArticuloArchivo> findByPath(String path);
    List<ArticuloArchivo> findByArticuloId(Long articuloId);
    List<ArticuloArchivo> findByArticuloIdAndTipo(Long articuloId, TipoArchivo tipoArchivo);
}
