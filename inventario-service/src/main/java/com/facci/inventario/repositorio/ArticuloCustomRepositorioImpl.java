package com.facci.inventario.repositorio;

import com.facci.comun.enums.TipoRelacion;
import com.facci.inventario.dto.ArticuloAsignacionDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ArticuloCustomRepositorioImpl implements ArticuloCustomRepositorio {

    private final EntityManager entityManager;

    public ArticuloCustomRepositorioImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public String obtenerSecuencialPorTipo(String tipo) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("JB_INV_Obtener_ActualizarSecuencial");
        query.registerStoredProcedureParameter("Tipo", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("NuevoSecuencial", String.class, ParameterMode.OUT);
        query.setParameter("Tipo", tipo);
        query.execute();
        return (String) query.getOutputParameterValue("NuevoSecuencial");
    }

    public List<ArticuloAsignacionDTO> obtenerAsignaciones(String filtroArticulo, String filtroUsuario, int offset, int limit) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("JB_INV_Obtener_asignaciones_con_detalles");
        query.registerStoredProcedureParameter("filtro_articulo", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("filtro_usuario", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("offset", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("limit", Integer.class, ParameterMode.IN);

        query.setParameter("filtro_articulo", filtroArticulo);
        query.setParameter("filtro_usuario", filtroUsuario);
        query.setParameter("offset", offset);
        query.setParameter("limit", limit);
        query.execute();
        List<Object[]> resultados = query.getResultList();

        return resultados.stream().map(row -> {
            ArticuloAsignacionDTO dto = new ArticuloAsignacionDTO();
            dto.setIdArticulo(((Number) row[0]).longValue());
            dto.setCodigoInterno((String) row[1]);
            dto.setCodigoOrigen((String) row[2]);
            dto.setNombreArticulo((String) row[3]);
            dto.setFechaAsignacion(row[4] != null ? ((java.sql.Timestamp) row[4]).toLocalDateTime() : null);
            dto.setIdUsuario(row[5] != null ? ((Number) row[5]).longValue() : null);
            dto.setTipoRelacion(row[6] != null ? TipoRelacion.valueOf((String) row[6]) : null);
            dto.setNombreAsignado((String) row[7]);
            return dto;
        }).collect(Collectors.toList());
    }

    public long contarAsignaciones(String filtroArticulo, String filtroUsuario) {
        String sql =
                "SELECT COUNT(*) " +
                        "FROM facci_inventario..articulo a " +
                        "LEFT JOIN facci_inventario..articulo_asignacion aa ON a.id = aa.articulo_id " +
                        "LEFT JOIN facci_configuracion..usuario u ON aa.id_usuario = u.id AND aa.tipo_relacion = 'USUARIO' " +
                        "LEFT JOIN facci_configuracion..area ar ON aa.id_usuario = ar.id AND aa.tipo_relacion = 'AREA' " +
                        "WHERE (?1 IS NULL OR a.nombre LIKE CONCAT('%', ?1, '%') OR a.codigo_origen LIKE CONCAT('%', ?1, '%')) " +
                        "AND (?2 IS NULL OR (aa.tipo_relacion = 'USUARIO' AND u.nombre_completo LIKE CONCAT('%', ?2, '%')) " +
                        "OR (aa.tipo_relacion = 'AREA' AND ar.nombre_area LIKE CONCAT('%', ?2, '%')))";

        Integer count = (Integer) entityManager.createNativeQuery(sql)
                .setParameter(1, filtroArticulo == null || filtroArticulo.isEmpty() ? null : filtroArticulo)
                .setParameter(2, filtroUsuario == null || filtroUsuario.isEmpty() ? null : filtroUsuario)
                .getSingleResult();

        return count.longValue();
    }

}
