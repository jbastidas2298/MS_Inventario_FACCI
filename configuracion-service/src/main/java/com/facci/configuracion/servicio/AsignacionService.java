package com.facci.configuracion.servicio;

import com.facci.comun.enums.TipoRelacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsignacionService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public boolean existeAsignacionAreaParaUsuario(TipoRelacion tipoRelacion, long idUsuario) {
        String sql = """
        SELECT 1 
        FROM facci_inventario..articulo_asignacion 
        WHERE tipo_relacion = :tipoRelacion AND id_usuario = :idUsuario
    """;

        List<?> result = entityManager.createNativeQuery(sql)
                .setParameter("tipoRelacion", tipoRelacion.name())
                .setParameter("idUsuario", idUsuario)
                .setMaxResults(1)
                .getResultList();

        return !result.isEmpty();
    }
}
