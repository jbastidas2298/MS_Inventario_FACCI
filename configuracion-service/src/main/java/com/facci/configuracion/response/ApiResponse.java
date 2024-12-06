package com.facci.configuracion.response;

import com.facci.configuracion.enums.EnumCodigos;

public class ApiResponse {
    private String codigo;
    private String mensaje;
    private Object data;

    public ApiResponse(EnumCodigos error, Object data) {
        this.codigo = error.getCodigo();
        this.mensaje = error.getDescripcion();
        this.data = data;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
