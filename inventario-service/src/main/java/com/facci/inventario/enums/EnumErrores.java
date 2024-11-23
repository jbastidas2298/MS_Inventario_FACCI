package com.facci.inventario.enums;

public enum EnumErrores {
    ARTICULO_YA_EXISTE("EI001", "El artículo ya existe."),
    ARTICULO_NO_ENCONTRADO("EI002", "El artículo no fue encontrado."),
    ARTICULO_ERROR_ACTUALIZAR("EI003", "Error al actualizar el artículo."),
    ARTICULO_ERROR_ELIMINAR("EI004", "Error al eliminar el artículo."),
    ARTICULO_ERROR_REGISTRAR("EI005", "Error al registrar el artículo."),
    IMAGEN_NO_VALIDA("EI006", "La imagen proporcionada no es válida."),
    IMAGEN_ERROR_GUARDAR("EI007", "Error al guardar la imagen."),
    CARPETA_NO_CREADA("EI008", "No se pudo crear la carpeta del artículo."),
    PDF_NO_VALIDO("EI009", "El archivo PDF proporcionado no es válido."),
    PDF_ERROR_GUARDAR("EI010", "Error al guardar el archivo PDF."),
    ARCHIVO_NO_VALIDO("EI011", "El archivo proporcionado no es válido."),
    IMAGEN_ARCHIVO_VACIO("EI012", "Archivo vacio."),
    ARCHIVO_NO_ENCONTRADO("EI013", "Archivo vacio."),
    PATH_INVALIDO("EI014", "Ruta Invalidad");



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
