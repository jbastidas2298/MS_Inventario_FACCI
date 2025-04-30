package com.facci.inventario.repositorio;

import com.facci.inventario.dto.ArticuloAsignacionDTO;
import com.facci.inventario.enums.EstadoArticulo;

import java.util.List;

public interface ArticuloCustomRepositorio {
    String obtenerSecuencialPorTipo(String tipo);
    List<ArticuloAsignacionDTO> obtenerAsignaciones(String filtroArticulo, String filtroUsuario, int offset, int limit);
    long contarAsignaciones(String filtroArticulo, String filtroUsuario);
    List<ArticuloAsignacionDTO> obtenerAsignacionesFiltrosCompletos(
            String filtroUsuario,
            EstadoArticulo estado,
            String grupoActivo,
            String nombre,
            String marca,
            String edificio,
            String seccion,
            int offset,
            int limit);
    long contarAsignacionesFiltros(
            String filtroUsuario,
            EstadoArticulo estado,
            String grupoActivo,
            String nombre,
            String marca,
            String edificio,
            String seccion);
}
