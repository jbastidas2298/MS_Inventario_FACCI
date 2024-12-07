package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.enums.TipoRelacion;

import java.util.List;
import java.util.Optional;

public interface ArticuloAsignacionRepositorio extends BaseRepositorio<ArticuloAsignacion>{

    List<ArticuloAsignacion> findByIdUsuario(Long idUsuario);

    Optional<ArticuloAsignacion> findByArticuloId(Long idArticulo);

    List<ArticuloAsignacion> findByIdUsuarioAndTipoRelacion(Long idUsuario, TipoRelacion tipoRelacion);

}
