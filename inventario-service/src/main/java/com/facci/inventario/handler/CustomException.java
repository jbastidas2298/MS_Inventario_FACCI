package com.facci.inventario.handler;

import com.facci.inventario.enums.EnumCodigos;

public class CustomException extends RuntimeException {

    private final String codigo;

    public CustomException(EnumCodigos error) {
        super(error.getDescripcion());
        this.codigo = error.getCodigo();
    }

    public String getCodigo() {
        return codigo;
    }
}

