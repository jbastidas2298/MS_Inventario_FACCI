package com.facci.configuracion.repositorio;

import com.facci.configuracion.dominio.Area;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaRepositorio extends BaseRepositorio<Area> {

    Optional<Area> findByNombreArea(String nombreArea);

}
