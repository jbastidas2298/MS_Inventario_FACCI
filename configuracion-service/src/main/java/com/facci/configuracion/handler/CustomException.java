package com.facci.configuracion.handler;

import com.facci.configuracion.enums.EnumErrores;

public class CustomException extends RuntimeException {

    private final String codigo;

    public CustomException(EnumErrores error) {
        super(error.getDescripcion());
        this.codigo = error.getCodigo();
    }

    public String getCodigo() {
        return codigo;
    }
}

