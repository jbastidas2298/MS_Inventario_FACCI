package com.facci.configuracion.repositorio;

import com.facci.configuracion.dominio.EntidadBaseId;
import org.springframework.data.repository.CrudRepository;


public interface BaseIdRepositorio<T extends EntidadBaseId> extends CrudRepository<T, Long>  {

}
