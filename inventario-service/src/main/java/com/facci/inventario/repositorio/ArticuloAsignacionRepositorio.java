package com.facci.inventario.repositorio;

import com.facci.comun.enums.TipoRelacion;
import com.facci.inventario.dominio.ArticuloAsignacion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticuloAsignacionRepositorio extends BaseRepositorio<ArticuloAsignacion>{
    List<ArticuloAsignacion> findByIdUsuario(Long idUsuario);

    Optional<ArticuloAsignacion> findByArticuloId(Long idArticulo);

    List<ArticuloAsignacion> findByIdUsuarioAndTipoRelacion(Long idUsuario, TipoRelacion tipoRelacion);

    @Query(value = "SELECT AA.*  FROM articulo_asignacion AA " +
            "INNER JOIN facci_configuracion..area A ON AA.id_usuario = A.id " +
            "WHERE AA.tipo_relacion = 'AREA' AND A.usuario_encargado_id = :idUsuario",
            nativeQuery = true)
    List<ArticuloAsignacion> findAsignacionesAreasUsuario(@Param("idUsuario") Long idUsuario);
}
