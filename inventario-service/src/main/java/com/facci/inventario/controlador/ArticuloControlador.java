package com.facci.inventario.controlador;

import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.dto.ArticuloDetalleDTO;
import com.facci.inventario.enums.EnumCodigos;
import com.facci.inventario.enums.TipoRelacion;
import com.facci.inventario.handler.CustomException;
import com.facci.inventario.response.ApiResponse;
import com.facci.inventario.servicio.ArchivoService;
import com.facci.inventario.servicio.ArticuloAsignacionService;
import com.facci.inventario.servicio.ArticuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventario/articulo/items")
@Tag(name = "Articulo", description = "Operaciones relacionadas con articulos")
public class ArticuloControlador {

    private final ArticuloService articuloService;
    private final ArchivoService archivoService;
    private final ArticuloAsignacionService articuloAsignacionService;

    public ArticuloControlador(ArticuloService articuloService, ArchivoService archivoService, ArticuloAsignacionService articuloAsignacionService) {
        this.articuloService = articuloService;
        this.archivoService = archivoService;
        this.articuloAsignacionService = articuloAsignacionService;
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
    public ResponseEntity<Map<String, Object>> subirImagen(@PathVariable Long id, @RequestParam("imagen") MultipartFile file) {
        try {
            String imagePath = archivoService.guardarImagen(id, file);
            return ResponseEntity.ok(
                    ApiResponse.buildResponse(
                            EnumCodigos.ARCHIVO_SUBIDO_EXITO
                    )
            );
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
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
                throw new CustomException(EnumCodigos.PATH_INVALIDO);
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
                throw new CustomException(EnumCodigos.PATH_INVALIDO);
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

    @PostMapping("/asignar")
    public ResponseEntity<List<ArticuloAsignacion>> asignarArticulos(
            @RequestParam Long idRelacionado,
            @RequestParam TipoRelacion tipoRelacion,
            @RequestBody List<Long> idsArticulos) {
        List<ArticuloAsignacion> asignaciones = articuloAsignacionService.asignarArticulos(idRelacionado, tipoRelacion, idsArticulos);
        return ResponseEntity.ok(asignaciones);
    }

    @PostMapping("/reasignar-todos")
    public ResponseEntity<List<ArticuloAsignacion>> reasignarArticulos(
            @RequestParam Long idUsuarioActual,
            @RequestParam Long idUsuarioNuevo,
            @RequestParam String descripcion) {
        List<ArticuloAsignacion> reasignaciones = articuloAsignacionService.reasignarArticulos(idUsuarioActual, idUsuarioNuevo, descripcion);
        return ResponseEntity.ok(reasignaciones);
    }

    @PostMapping("/reasignar-articulo")
    public ResponseEntity<ArticuloAsignacion> reasignarArticulo(
            @RequestParam Long idArticulo,
            @RequestParam Long idUsuarioNuevo,
            @RequestParam String descripcion) {
        ArticuloAsignacion reasignacion = articuloAsignacionService.reasignarArticulo(idArticulo, idUsuarioNuevo, descripcion);
        return ResponseEntity.ok(reasignacion);
    }

    @GetMapping("/{id}/codigo-barras")
    @Operation(summary = "Generar código de barras", description = "Genera un código de barras para el artículo dado su ID")
    public ResponseEntity<byte[]> generarCodigoBarra(@PathVariable Long id) {
        byte[] codigoBarras = articuloService.generarCodigoBarra(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(codigoBarras.length);

        return new ResponseEntity<>(codigoBarras, headers, HttpStatus.OK);
    }

    @PostMapping("/codigo-barras/reporte")
    public ResponseEntity<byte[]> generarReporteCodigosBarra(@RequestBody List<Long> articuloIds) {
        byte[] pdfReporte = articuloService.generarReporteCodigosBarra(articuloIds);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("reporte_codigos_barras.pdf")
                .build());

        return new ResponseEntity<>(pdfReporte, headers, HttpStatus.OK);
    }

    @PostMapping("/reporteArticulo/{id}")
    public ResponseEntity<byte[]> generarReporteArticulo(@PathVariable Long id) {
        byte[] pdfReporte = articuloService.generarReporteArticuloCompleto(id);
        ArticuloDTO articuloDTO = articuloService.consultarArticulo(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("Reporte_"+articuloDTO.getCodigoInterno()+".pdf")
                .build());

        return new ResponseEntity<>(pdfReporte, headers, HttpStatus.OK);
    }

    @GetMapping("/articuloDetalle/{id}")
    @Operation(summary = "Obtener Modelo de articulo ", description = "Metodo para obtener el modelo completo del articulo")
    public ArticuloDetalleDTO consultarArticuloDetalle(@PathVariable Long id) {
        return articuloService.obtenerArticuloDetalle(id);
    }
}
