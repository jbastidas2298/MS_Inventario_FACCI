package com.facci.inventario.servicio;

import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.handler.NotFoundException;
import com.facci.inventario.map.ArticuloMapper;
import com.facci.inventario.repositorio.ArticuloRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ArticuloService {

    private final ArticuloRepositorio articuloRepositorio;
    private final ArticuloMapper articuloMapper;

    public ArticuloService(ArticuloRepositorio articuloRepositorio, ArticuloMapper articuloMapper) {
        this.articuloRepositorio = articuloRepositorio;
        this.articuloMapper = articuloMapper;
    }

    public Mono<ResponseEntity<?>> registrar(ArticuloDTO dto) {
        return Mono.fromCallable(() -> {
            Articulo nuevoArticulo = new Articulo(dto);
            Articulo articuloGuardado = articuloRepositorio.save(nuevoArticulo); // Guardar en la base de datos
            log.debug("Artículo registrado: {}", articuloGuardado.getNombre());
            return ResponseEntity.ok(articuloGuardado);
        });
    }

    @Transactional
    public ResponseEntity<?> actualizar(ArticuloDTO articuloDTO) {
        var articuloOp = articuloRepositorio.findById(articuloDTO.getId());

        if (articuloOp.isEmpty()) {
            String mensajeError = "Artículo no encontrado con id: " + articuloDTO.getId();
            log.error(mensajeError);
            throw new NotFoundException();
        }

        var articuloExistente = articuloOp.get();
        try {
            articuloExistente.setNombre(articuloDTO.getNombre());
            articuloExistente.setDescripcion(articuloDTO.getDescripcion());
            log.info("Artículo modificado: {}", articuloExistente);
            return ResponseEntity.ok(articuloMapper.mapToDto(articuloExistente));
        } catch (Exception e) {
            String mensajeError = "Error al actualizar el artículo: " + articuloExistente.getNombre();
            log.error(mensajeError, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajeError);
        }
    }

    @Transactional
    public ResponseEntity<?> eliminar(Long id) {
        var articuloOp = articuloRepositorio.findById(id);

        if (articuloOp.isEmpty()) {
            String mensajeError = "Artículo no encontrado con id: " + id;
            log.error(mensajeError);
            throw new NotFoundException();
        }
        try {
            articuloRepositorio.delete(articuloOp.get());
            log.info("Artículo eliminado con id: {}", id);
            return ResponseEntity.ok("Artículo eliminado exitosamente.");
        } catch (Exception e) {
            String mensajeError = "Error al eliminar el artículo con id: " + id;
            log.error(mensajeError, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensajeError);
        }
    }

    public ResponseEntity<ArticuloDTO> consultarArticulo(long id) {
        var articuloOp = articuloRepositorio.findById(id);

        if (articuloOp.isEmpty()) {
            String mensajeError = "Artículo no encontrado con id: " + id;
            log.error(mensajeError);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            ArticuloDTO articuloDTO = articuloMapper.mapToDto(articuloOp.get());
            return ResponseEntity.ok(articuloDTO);
        } catch (Exception e) {
            String mensajeError = "Error al obtener el artículo con id: " + id;
            log.error(mensajeError, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<ArticuloDTO>> consultarTodos() {
        List<Articulo> articulos = StreamSupport
                .stream(articuloRepositorio.findAll().spliterator(), false)
                .collect(Collectors.toList());

        if (articulos.isEmpty()) {
            log.info("No se encontraron artículos registrados.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<ArticuloDTO> articulosDto = articulos.stream()
                .map(articuloMapper::mapToDto)
                .collect(Collectors.toList());

        log.info("Artículos consultados: {}", articulosDto.size());
        return ResponseEntity.ok(articulosDto);
    }
}
