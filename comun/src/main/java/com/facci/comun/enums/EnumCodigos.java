package com.facci.comun.enums;

public enum EnumCodigos {
    //Usuario
    USUARIO_YA_EXISTE("EC001", "El usuario ya existe."),
    AREA_YA_EXISTE("EC002", "El área ya existe."),
    USUARIO_NO_ENCONTRADO("EC003", "Usuario no encontrado."),
    AREA_NO_ENCONTRADA("EC004", "Área no encontrada."),
    ERROR_INICIO("EC005", "Inicio no Autorizado"),
    ERROR_CREDENCIALES("EC006", "Credenciales incorrectas. Verifica tu nombre de usuario y contraseña."),
    USUARIO_ELIMINADO("EC007", "Usuario Eliminado."),
    ERROR_ELIMINAR_USUARIO("EC007", "Error al eliminar Usuario"),
    ERROR_CONSULTA_USUARIO("EC007", "Error al consultar Usuario"),
    ERROR_GENERAR_NOMBRE_USUARIO("EC008", "Error al generar nombre de usuario, solicitado minimo 1 nombre y 2 apellido"),
    ERROR_IMPORTAR_USUARIOS("EC009", "Error al importar usuarios"),
    ERROR_ACTUALIZAR_USUARIO("EC010", "Error al actualizar usuario"),
    //Inventario
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
    PATH_INVALIDO("EI014", "Ruta Invalidad"),
    ASIGNACIONES_NO_ENCONTRADAS("EI015","No se encontraron artículos asignados al usuario actual."),
    USUARIO_ASIGNAR_EN_SESION("EI016","El usuario en sesion no es valido par Asignarse Articulos."),
    CODIGO_BARRAS_GENERAR("EI017","Error al generar codigo de barras"),
    REPORTE_NO_ENCONTRADO("EI018","EL reporte no fue encontrado"),
    REPORTE_ERROR_GENERAR("EI019","Error al generar el reporte"),
    ARTICULO_ASIGNADO_NO_ELIMINABLE("EI020","Articulo Asigando no es posible eliminar"),
    ERROR_COMBINAR_PDFS("EI021","Error al combinar PDF"),
    ARCHIVO_SUBIDO_EXITO("EI022","Archivo cargado con exito");



    private final String codigo;
    private final String descripcion;

    EnumCodigos(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EnumCodigos fromCodigo(String codigo) {
        for (EnumCodigos error : values()) {
            if (error.codigo.equals(codigo)) {
                return error;
            }
        }
        throw new IllegalArgumentException("Código de error no válido: " + codigo);
    }
}
