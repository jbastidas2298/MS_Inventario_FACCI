package com.facci.inventario.response;

import com.facci.inventario.enums.EnumCodigos;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
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

    public static Map<String, Object> buildResponse(EnumCodigos codigoEnum, Map<String, Object> additionalData) {
        Map<String, Object> response = buildResponse(codigoEnum);
        if (additionalData != null) {
            response.putAll(additionalData);
        }
        return response;
    }
}
