package com.facci.inventario.repositorio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

@Repository
public class SecuencialCustomRepositorioImpl implements SecuencialCustomRepositorio {

    private final EntityManager entityManager;

    public SecuencialCustomRepositorioImpl(EntityManager entityManager) {
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
}
