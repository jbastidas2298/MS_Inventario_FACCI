package com.facci.inventario.servicio;

import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.enums.EnumErrores;
import com.facci.inventario.handler.CustomException;
import com.facci.inventario.map.ArticuloMapper;
import com.facci.inventario.repositorio.ArticuloRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ArticuloService {

    private final ArticuloRepositorio articuloRepositorio;
    private final ArticuloMapper articuloMapper;
    private final SecuencialService secuencialService;


    public ArticuloService(ArticuloRepositorio articuloRepositorio, ArticuloMapper articuloMapper, SecuencialService secuencialService) {
        this.articuloRepositorio = articuloRepositorio;
        this.articuloMapper = articuloMapper;
        this.secuencialService = secuencialService;
    }

    public ArticuloDTO registrar(ArticuloDTO dto) {
        Optional<Articulo> articuloExistente = articuloRepositorio.findByCodigoOrigen(dto.getCodigoOrigen());
        if (articuloExistente.isPresent()) {
            log.error("El artículo con código de origen '{}' ya existe.", dto.getCodigoOrigen());
            throw new CustomException(EnumErrores.ARTICULO_YA_EXISTE);
        }
        String secuencial = secuencialService.generarSecuencial("Articulo");
        dto.setCodigoInterno(secuencial);
        Articulo nuevoArticulo = new Articulo(dto);
        Articulo articuloGuardado = articuloRepositorio.save(nuevoArticulo);

        log.debug("Artículo registrado: {}", articuloGuardado.getNombre());
        return articuloMapper.mapToDto(articuloGuardado);
    }

    public ArticuloDTO actualizar(ArticuloDTO articuloDTO) {
        Articulo articuloExistente = articuloRepositorio.findById(articuloDTO.getId())
                .orElseThrow(() -> new CustomException(EnumErrores.ARTICULO_NO_ENCONTRADO));

        articuloExistente.setNombre(articuloDTO.getNombre());
        articuloExistente.setDescripcion(articuloDTO.getDescripcion());
        Articulo articuloActualizado = articuloRepositorio.save(articuloExistente);

        log.info("Artículo actualizado por usuario: {}", articuloActualizado.getNombre());
        return articuloMapper.mapToDto(articuloActualizado);
    }

    public void eliminar(Long id) {
        Articulo articulo = articuloRepositorio.findById(id)
                .orElseThrow(() -> new CustomException(EnumErrores.ARTICULO_NO_ENCONTRADO));
        articuloRepositorio.delete(articulo);
        log.info("Artículo eliminado con id: {}", id);
    }

    public ArticuloDTO consultarArticulo(long id) {
        Articulo articulo = articuloRepositorio.findById(id)
                .orElseThrow(() -> new CustomException(EnumErrores.ARTICULO_NO_ENCONTRADO));
        return articuloMapper.mapToDto(articulo);
    }

    public List<ArticuloDTO> consultarTodos() {
        List<Articulo> articulos = StreamSupport.stream(articuloRepositorio.findAll().spliterator(), false)
                .collect(Collectors.toList());
        log.info("Artículos consultados: {}", articulos.size());
        return articulos.stream()
                .map(articuloMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
