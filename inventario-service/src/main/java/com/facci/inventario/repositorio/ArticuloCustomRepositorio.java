package com.facci.inventario.repositorio;

import com.facci.inventario.dto.ArticuloAsignacionDTO;

import java.util.List;

public interface ArticuloCustomRepositorio {
    String obtenerSecuencialPorTipo(String tipo);
    List<ArticuloAsignacionDTO> obtenerAsignaciones(String filtroArticulo, String filtroUsuario, int offset, int limit);
    long contarAsignaciones(String filtroArticulo, String filtroUsuario);
}
