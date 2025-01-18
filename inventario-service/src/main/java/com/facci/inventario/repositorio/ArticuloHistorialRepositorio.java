package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.ArticuloHistorial;

import java.util.List;

public interface ArticuloHistorialRepositorio extends BaseRepositorio<ArticuloHistorial>{
    List<ArticuloHistorial> findByArticulo_Id(Long idArticulo);
}
