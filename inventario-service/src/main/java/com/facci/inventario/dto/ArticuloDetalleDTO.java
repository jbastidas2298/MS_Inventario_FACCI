package com.facci.inventario.dto;

import com.facci.comun.dto.UsuarioDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Getter
@Setter
public class ArticuloDetalleDTO {
    private ArticuloDTO articulo;
    private List<ArticuloArchivoDTO> archivos;
    private List<ArticuloHistorialDTO> historial;
    private UsuarioDTO usuarioAsignado;
}
