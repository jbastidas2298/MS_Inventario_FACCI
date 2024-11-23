package com.facci.inventario.enums;

public enum EnumErrores {
    ARTICULO_YA_EXISTE("EI001", "El artículo ya existe."),
    ARTICULO_NO_ENCONTRADO("EI002", "El artículo no fue encontrado."),
    ARTICULO_ERROR_ACTUALIZAR("EI003", "Error al actualizar el artículo."),
    ARTICULO_ERROR_ELIMINAR("EI004", "Error al eliminar el artículo."),
    ARTICULO_ERROR_REGISTRAR("EI005", "Error al registrar el artículo.");
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
