package com.facci.inventario.repositorio;

import com.facci.comun.enums.TipoRelacion;
import com.facci.inventario.dto.ArticuloAsignacionDTO;
import com.facci.inventario.enums.EstadoArticulo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.apache.commons.lang3.StringUtils;
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
            dto.setEstadoArticulo(row[8] != null ? EstadoArticulo.valueOf((String) row[8]) : null);
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

    public List<ArticuloAsignacionDTO> obtenerAsignacionesFiltrosCompletos(
            long filtroUsuario,
            EstadoArticulo estado,
            String grupoActivo,
            String nombre,
            String marca,
            String edificio,
            String seccion,
            int offset,
            int limit) {

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(
                "JB_INV_Obtener_asignaciones_con_detalles_completo");

        query.registerStoredProcedureParameter("filtro_usuario", long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("estado", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("grupo_activo", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("nombre", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("marca", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("edificio", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("seccion", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("offset", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("limit", Integer.class, ParameterMode.IN);

        query.setParameter("filtro_usuario", filtroUsuario > 0 ? filtroUsuario: null);
        query.setParameter("estado", estado == null ? null : estado.name());
        query.setParameter("grupo_activo", StringUtils.isBlank(grupoActivo) ? null : grupoActivo);
        query.setParameter("nombre", StringUtils.isBlank(nombre) ? null : nombre);
        query.setParameter("marca", StringUtils.isBlank(marca) ? null : marca);
        query.setParameter("edificio", StringUtils.isBlank(edificio) ? null : edificio);
        query.setParameter("seccion", StringUtils.isBlank(seccion) ? null : seccion);
        query.setParameter("offset", offset);
        query.setParameter("limit", limit);

        query.execute();
        List<Object[]> resultados = query.getResultList();

        return resultados.stream().map(row -> {
            ArticuloAsignacionDTO dto = new ArticuloAsignacionDTO();

            dto.setIdUsuario(row[0] != null ? ((Number) row[0]).longValue() : null);
            dto.setNombreAsignado((String) row[1]);
            dto.setIdArticulo(((Number) row[2]).longValue());
            dto.setCodigoInterno((String) row[3]);
            dto.setCodigoOrigen((String) row[4]);
            dto.setFechaAsignacion(row[5] != null ? ((java.sql.Timestamp) row[5]).toLocalDateTime() : null);
            dto.setTipoRelacion(row[6] != null ? TipoRelacion.valueOf((String) row[6]) : null);
            dto.setEstadoArticulo(row[7] != null ? EstadoArticulo.valueOf((String) row[7]) : null);
            dto.setNombreArticulo((String) row[8]);
            dto.setMarcaArticulo((String) row[9]);
            dto.setSerieArticulo((String) row[10]);
            dto.setModeloArticulo((String) row[11]);
            dto.setUbicacionArticulo((String) row[12]);
            dto.setSeccionArticulo((String) row[13]);
            dto.setGrupoActivo((String) row[14]);
            dto.setDescripcion((String) row[15]);
            dto.setFechaEstado(row[16] != null ? ((java.sql.Timestamp) row[16]).toLocalDateTime() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    public long contarAsignacionesFiltros(
            long filtroUsuario,
            EstadoArticulo estado,
            String grupoActivo,
            String nombre,
            String marca,
            String edificio,
            String seccion) {

        String sql =
                "SELECT COUNT(DISTINCT a.id) " +
                        "FROM facci_inventario..articulo a " +
                        "LEFT JOIN facci_inventario..articulo_asignacion aa ON a.id = aa.articulo_id " +
                        "LEFT JOIN facci_configuracion..usuario u ON aa.id_usuario = u.id AND aa.tipo_relacion = 'USUARIO' " +
                        "LEFT JOIN facci_configuracion..area ar ON aa.id_usuario = ar.id AND aa.tipo_relacion = 'AREA' " +
                        "WHERE ((?1 IS NULL OR (aa.tipo_relacion = 'USUARIO' AND u.id  = ?1)) " +
                        "OR (aa.tipo_relacion = 'AREA' AND ar.id  = ?1)) " +
                        "AND (?2 IS NULL OR a.estado = ?2) " +
                        "AND (?3 IS NULL OR a.grupo_activo_id = ?3) " +
                        "AND (?4 IS NULL OR a.nombre LIKE CONCAT('%', ?4, '%')) " +
                        "AND (?5 IS NULL OR a.marca = ?5) " +
                        "AND (?6 IS NULL OR a.ubicacion LIKE CONCAT('%', ?6, '%')) " +
                        "AND (?7 IS NULL OR a.seccion = ?7)";
        return ((Number) entityManager.createNativeQuery(sql)
                .setParameter(1, filtroUsuario > 0 ? filtroUsuario: null)
                .setParameter(2, estado == null ? null : estado.name())
                .setParameter(3, StringUtils.isBlank(grupoActivo) ? null : grupoActivo)
                .setParameter(4, StringUtils.isBlank(nombre) ? null : nombre)
                .setParameter(5, StringUtils.isBlank(marca) ? null : marca)
                .setParameter(6, StringUtils.isBlank(edificio) ? null : edificio)
                .setParameter(7, StringUtils.isBlank(seccion) ? null : seccion)
                .getSingleResult()).longValue();
    }
}
