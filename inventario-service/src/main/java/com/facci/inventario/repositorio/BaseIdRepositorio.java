package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.EntidadBaseId;
import org.springframework.data.repository.CrudRepository;


public interface BaseIdRepositorio<T extends EntidadBaseId> extends CrudRepository<T, Long>  {

}
