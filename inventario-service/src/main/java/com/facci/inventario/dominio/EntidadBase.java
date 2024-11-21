package com.facci.inventario.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuppressWarnings("serial")
public abstract class EntidadBase extends EntidadBaseId {
    public static final String PROPIEDAD_FECHA_CREACION = "fechaCreacion";
    public static final String PROPIEDAD_FECHA_MODIFICACION = "fechaModificacion";

    @CreatedDate
    @JsonIgnore
    private LocalDateTime creadoFecha;

    @CreatedBy
    @NotNull
    @JsonIgnore
    private String creadoPor;

    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime modificadoFecha;

    @LastModifiedBy
    @NotNull
    @JsonIgnore
    private String modificadoPor;

    public LocalDateTime getCreadoFecha() {
        return creadoFecha;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public LocalDateTime getModificadoFecha() {
        return modificadoFecha;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setCreadoFecha(LocalDateTime creadoFecha) {
        this.creadoFecha = creadoFecha;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

}
