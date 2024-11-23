package com.facci.inventario.servicio;

import com.facci.inventario.repositorio.SecuencialCustomRepositorio;
import org.springframework.stereotype.Service;

@Service
public class SecuencialService {

    private final SecuencialCustomRepositorio secuencialCustomRepositorio;

    public SecuencialService(SecuencialCustomRepositorio secuencialCustomRepositorio) {
        this.secuencialCustomRepositorio = secuencialCustomRepositorio;
    }

    public String generarSecuencial(String tipo) {
        return secuencialCustomRepositorio.obtenerSecuencialPorTipo(tipo);
    }
}
