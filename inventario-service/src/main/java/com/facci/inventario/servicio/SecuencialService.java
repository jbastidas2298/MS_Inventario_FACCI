package com.facci.inventario.servicio;

import com.facci.inventario.repositorio.ArticuloCustomRepositorio;
import org.springframework.stereotype.Service;

@Service
public class SecuencialService {

    private final ArticuloCustomRepositorio articuloCustomRepositorio;

    public SecuencialService(ArticuloCustomRepositorio articuloCustomRepositorio) {
        this.articuloCustomRepositorio = articuloCustomRepositorio;
    }

    public String generarSecuencial(String tipo) {
        return articuloCustomRepositorio.obtenerSecuencialPorTipo(tipo);
    }
}
