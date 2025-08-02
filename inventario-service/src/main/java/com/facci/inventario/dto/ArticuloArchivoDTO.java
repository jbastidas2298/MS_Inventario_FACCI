package com.facci.inventario.dto;

import com.facci.inventario.enums.TipoArchivo;
import lombok.*;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
public class ArticuloArchivoDTO {
    private long id;
    private String path;
    private TipoArchivo tipo;
}
