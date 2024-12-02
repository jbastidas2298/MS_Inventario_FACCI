package com.facci.configuracion.enums;

public enum EnumCodigos {
    USUARIO_YA_EXISTE("EC001", "El usuario ya existe."),
    AREA_YA_EXISTE("EC002", "El área ya existe."),
    USUARIO_NO_ENCONTRADO("EC003", "Usuario no encontrado."),
    AREA_NO_ENCONTRADA("EC004", "Área no encontrada."),
    ERROR_INICIO("EC005", "Inicio no Autorizado"),
    ERROR_CREDENCIALES("EC006", "Credenciales incorrectas. Verifica tu nombre de usuario y contraseña."),
    USUARIO_ELIMINADO("EC007", "Usuario Eliminado."),
    ERROR_ELIMINAR_USUARIO("EC007", "Error al eliminar Usuario"),
    ERROR_CONSULTA_USUARIO("EC007", "Error al consultar Usuario");



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
