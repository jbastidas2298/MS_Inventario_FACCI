package com.facci.inventario.servicio;

import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloHistorial;
import com.facci.inventario.dto.UsuarioDTO;
import com.facci.inventario.enums.TipoOperacion;
import com.facci.inventario.repositorio.ArticuloHistorialRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ArticuloHistorialService {

    private final ArticuloHistorialRepositorio articuloHistorialRepositorio;

    public ArticuloHistorialService(ArticuloHistorialRepositorio articuloHistorialRepositorio) {
        this.articuloHistorialRepositorio = articuloHistorialRepositorio;
    }

    public void registrarEvento(Articulo articulo, TipoOperacion tipoOperacion, String descripcion, UsuarioDTO usuarioDTO) {
        String descripcionN;
        if(descripcion == null){
            descripcionN = generarDescripcion(tipoOperacion, articulo, usuarioDTO);
        }else{
            descripcionN = descripcion;
        }
        log.info("Registrando evento para artículo con ID {} y operación {}", articulo.getId(), tipoOperacion);
        ArticuloHistorial historial = ArticuloHistorial.builder()
                .idArticulo(articulo.getId())
                .codigoInterno(articulo.getCodigoInterno())
                .tipoOperacion(tipoOperacion)
                .descripcion(descripcionN)
                .build();
        articuloHistorialRepositorio.save(historial);
    }
    private String generarDescripcion(TipoOperacion tipoOperacion, Articulo articulo,UsuarioDTO usuarioDTO) {
        switch (tipoOperacion) {
            case INGRESO:
                return "Se ingresó un nuevo artículo con código interno: " + articulo.getCodigoInterno() + " por el usuario "+ usuarioDTO.getNombreCompleto();
            case ACTUALIZACION:
                return "Se actualizó el artículo con código interno: " + articulo.getCodigoInterno() + " por el usuario "+ usuarioDTO.getNombreCompleto();
            case ASIGNACION:
                return "Se asignó el artículo con código interno: " + articulo.getCodigoInterno() + " a "+ usuarioDTO.getNombreCompleto();
            case REASIGNACION:
                return "Se reasigno el artículo con código interno: " + articulo.getCodigoInterno() + " a "+ usuarioDTO.getNombreCompleto();
            case ELIMINACION:
                return "Se eliminó el artículo código interno: " + articulo.getCodigoInterno();
            default:
                return "Operación no especificada código interno: " + articulo.getCodigoInterno();
        }
    }
}
