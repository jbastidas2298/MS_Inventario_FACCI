package com.facci.inventario.controlador;

import com.facci.comun.enums.EnumCodigos;
import com.facci.comun.handler.CustomException;
import com.facci.comun.response.ApiResponse;
import com.facci.inventario.dto.ArticuloDTO;
import com.facci.inventario.servicio.ArchivoService;
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
@RequestMapping("/inventario/articulo/archivo")
@Tag(name = "Reportes", description = "Operaciones relacionadas con reportes, imagenes, pdf")
public class ArchivoControlador {

    private final ArticuloService articuloService;
    private final ArchivoService archivoService;

    public ArchivoControlador(ArticuloService articuloService, ArchivoService archivoService) {
        this.articuloService = articuloService;
        this.archivoService = archivoService;
    }
    @GetMapping("/{id}/codigo-barras")
    @Operation(summary = "Generar código de barras", description = "Genera un código de barras en formato PNG para un artículo dado su ID")
    public ResponseEntity<byte[]> generarCodigoBarra(@PathVariable Long id) {
        byte[] codigoBarras = archivoService.generarCodigoBarra(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(codigoBarras.length);

        return new ResponseEntity<>(codigoBarras, headers, HttpStatus.OK);
    }

    @PostMapping("/codigo-barras/reporte")
    @Operation(summary = "Generar reporte de códigos de barras", description = "Genera un reporte PDF con los códigos de barras de múltiples artículos")
    public ResponseEntity<byte[]> generarReporteCodigosBarra(@RequestBody List<Long> articuloIds) {
        byte[] pdfReporte = archivoService.generarReporteCodigosBarra(articuloIds);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("reporte_codigos_barras.pdf")
                .build());

        return new ResponseEntity<>(pdfReporte, headers, HttpStatus.OK);
    }

    @PostMapping("/reporteArticulo/{id}")
    @Operation(summary = "Generar reporte completo de artículo", description = "Genera un reporte PDF con toda la información de un artículo dado su ID")
    public ResponseEntity<byte[]> generarReporteArticulo(@PathVariable Long id) {
        var pdfReporte = archivoService.generarReporteArticuloCompleto(id);
        ArticuloDTO articuloDTO = articuloService.consultarArticulo(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("Reporte_" + articuloDTO.getCodigoInterno() + ".pdf")
                .build());

        return new ResponseEntity<>(pdfReporte, headers, HttpStatus.OK);
    }

    @PostMapping("/reporteActaEntrega/{id}")
    @Operation(summary = "Generar acta de entrega", description = "Genera un acta de entrega en formato PDF para un artículo dado su ID")
    public ResponseEntity<byte[]> generarReporteActaEntrega(@PathVariable Long id) {
        var pdfReporte = archivoService.generarReporteActaEntrega(id);
        ArticuloDTO articuloDTO = articuloService.consultarArticulo(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("Reporte_Acta_Entrega" + articuloDTO.getCodigoInterno() + ".pdf")
                .build());

        return new ResponseEntity<>(pdfReporte, headers, HttpStatus.OK);
    }

    @PostMapping("/{id}/imagen")
    @Operation(summary = "Subir imagen de un artículo", description = "Permite subir una imagen asociada a un artículo mediante su ID")
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
    @Operation(summary = "Subir PDF de un artículo", description = "Permite subir un archivo PDF asociado a un artículo mediante su ID")
    public ResponseEntity<Map<String, Object>> subirPdf(@PathVariable Long id, @RequestParam("pdf") MultipartFile file) {
        try {
            String pdfPath = archivoService.guardarPdf(id, file);
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

    @PostMapping("/archivo/descargar")
    @Operation(summary = "Descargar archivo", description = "Permite descargar un archivo almacenado en el sistema mediante su ruta")
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
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/archivo/ver")
    @Operation(summary = "Visualizar archivo", description = "Permite visualizar un archivo almacenado en el sistema mediante su ruta")
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
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/importar-excel")
    @Operation(summary = "Articulos Excel", description = "Importa masivamente articulos desde formato excel")
    public ResponseEntity<List<ArticuloDTO>> importarExcel(@RequestParam("file") MultipartFile file) {
        try {
            var usuariosProcesados= archivoService.procesarExcel(file);
            return ResponseEntity.ok(usuariosProcesados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
