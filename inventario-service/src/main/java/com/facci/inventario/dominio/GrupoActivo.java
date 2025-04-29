package com.facci.inventario.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
public class GrupoActivo extends EntidadBase{
    @Column(unique = true, nullable = false)
    String codigo;
    String descripcion;
}
