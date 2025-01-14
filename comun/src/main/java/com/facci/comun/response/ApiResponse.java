package com.facci.comun.response;

import com.facci.comun.enums.EnumCodigos;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
    private String codigo;
    private String mensaje;
    private Object data;

    public static Map<String, Object> buildResponse(EnumCodigos codigoEnum) {
        Map<String, Object> response = new HashMap<>();
        response.put("codigo", codigoEnum.getCodigo());
        response.put("descripcion", codigoEnum.getDescripcion());
        return response;
    }

    public static Map<String, Object> buildResponse(EnumCodigos codigoEnum, String key, Object value) {
        Map<String, Object> response = buildResponse(codigoEnum);
        response.put(key, value);
        return response;
    }

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
