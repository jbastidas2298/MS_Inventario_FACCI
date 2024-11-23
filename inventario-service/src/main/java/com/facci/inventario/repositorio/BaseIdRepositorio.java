package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.EntidadBaseId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface BaseIdRepositorio<T extends EntidadBaseId> extends CrudRepository<T, Long> {

}
