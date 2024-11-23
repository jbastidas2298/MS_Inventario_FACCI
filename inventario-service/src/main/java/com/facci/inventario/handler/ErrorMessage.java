package com.facci.inventario.handler;

public class ErrorMessage {
    private final String codigo;
    private final String mensaje;

    public ErrorMessage(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
