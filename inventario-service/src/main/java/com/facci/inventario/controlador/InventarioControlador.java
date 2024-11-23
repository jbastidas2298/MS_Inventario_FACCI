package com.facci.inventario.controlador;

import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.enums.EnumErrores;
import com.facci.inventario.handler.CustomException;
import com.facci.inventario.servicio.ArchivoService;
import com.facci.inventario.servicio.ArticuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/inventario/articulo/items")
@Tag(name = "Articulo", description = "Operaciones relacionadas con articulos")
public class InventarioControlador {

    private final ArticuloService articuloService;
    private final ArchivoService archivoService;


    public InventarioControlador(ArticuloService articuloService, ArchivoService archivoService) {
        this.articuloService = articuloService;
        this.archivoService = archivoService;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo artículo", description = "Registra un nuevo artículo en el sistema")
    public ResponseEntity<ArticuloDTO> registrar(@RequestBody ArticuloDTO dto) {
        ArticuloDTO articuloRegistrado = articuloService.registrar(dto);
        return ResponseEntity.ok(articuloRegistrado);
    }

    @PutMapping
    @Operation(summary = "Actualizar un artículo existente", description = "Actualiza la información de un artículo ya registrado")
    public ResponseEntity<ArticuloDTO> actualizar(@RequestBody ArticuloDTO dto) {
        ArticuloDTO articuloActualizado = articuloService.actualizar(dto);
        return ResponseEntity.ok(articuloActualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un artículo existente", description = "Elimina un artículo ya registrado en el sistema")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        articuloService.eliminar(id);
        return ResponseEntity.ok("Artículo eliminado exitosamente.");
    }

    @GetMapping
    @Operation(summary = "Consultar todos los artículos", description = "Obtiene una lista de todos los artículos registrados en el sistema")
    public List<ArticuloDTO> consultarTodos() {
        return articuloService.consultarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar artículo por ID", description = "Obtiene un artículo registrado en el sistema")
    public ResponseEntity<ArticuloDTO> consultarArticulo(@PathVariable Long id) {
        ArticuloDTO articulo = articuloService.consultarArticulo(id);
        return ResponseEntity.ok(articulo);
    }

    @PostMapping("/{id}/imagen")
    public ResponseEntity<String> subirImagen(@PathVariable Long id, @RequestParam("imagen") MultipartFile file) {
        try {
            String imagePath = archivoService.guardarImagen(id, file);
            return ResponseEntity.ok(imagePath);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }


    @PostMapping("/{id}/pdf")
    public ResponseEntity<String> subirPdf(@PathVariable Long id, @RequestParam("pdf") MultipartFile file) {
        try {
            String pdfPath = archivoService.guardarPdf(id, file);
            return ResponseEntity.ok("PDF guardado exitosamente en: " + pdfPath);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el PDF: " + e.getMessage());
        }
    }

    @GetMapping("/archivo/descargar")
    public ResponseEntity<Resource> descargarArchivo(@RequestBody String path) {
        try {
            if (path == null || path.isEmpty()) {
                throw new CustomException(EnumErrores.PATH_INVALIDO);
            }

            Resource file = archivoService.obtenerArchivo(path);
            String fileName = Paths.get(path).getFileName().toString();
            String contentType = Files.probeContentType(Paths.get(path));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"") // inline permite visualizar
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/archivo/ver")
    public ResponseEntity<Resource> visualizarArchivo(@RequestBody String path) {
        try {
            if (path == null || path.isEmpty()) {
                throw new CustomException(EnumErrores.PATH_INVALIDO);
            }

            Resource file = archivoService.obtenerArchivo(path);
            String fileName = Paths.get(path).getFileName().toString();
            String contentType = Files.probeContentType(Paths.get(path));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"") // inline permite visualizar
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
