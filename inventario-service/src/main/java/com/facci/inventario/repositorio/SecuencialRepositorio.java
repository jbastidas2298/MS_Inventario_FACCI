package com.facci.inventario.repositorio;

import com.facci.inventario.dominio.Secuencial;

import java.util.Optional;

public interface SecuencialRepositorio extends BaseRepositorio<Secuencial>{
    Optional<Secuencial> findByTipo(String tipo);
}
