package com.facci.configuracion.enums;

public enum EnumErrores {
    USUARIO_YA_EXISTE("EC001", "El usuario ya existe."),
    AREA_YA_EXISTE("EC002", "El área ya existe."),
    USUARIO_NO_ENCONTRADO("EC003", "Usuario no encontrado."),
    AREA_NO_ENCONTRADA("EC004", "Área no encontrada."),
    ERROR_INICIO_SECION("EC005", "Inicio no Autorizado");

    private final String codigo;
    private final String descripcion;

    EnumErrores(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EnumErrores fromCodigo(String codigo) {
        for (EnumErrores error : values()) {
            if (error.codigo.equals(codigo)) {
                return error;
            }
        }
        throw new IllegalArgumentException("Código de error no válido: " + codigo);
    }
}
