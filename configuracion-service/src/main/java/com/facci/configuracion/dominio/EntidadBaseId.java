package com.facci.configuracion.dominio;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;

@MappedSuperclass
@SuppressWarnings("serial")
public abstract class EntidadBaseId implements Serializable {
    public static final String PROPIEDAD_ID="id";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
