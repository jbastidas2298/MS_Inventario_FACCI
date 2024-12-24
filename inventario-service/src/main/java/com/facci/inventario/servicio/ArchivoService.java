package com.facci.inventario.servicio;

import com.facci.inventario.Configuracion.ConfiguracionService;
import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloArchivo;
import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.dto.*;
import com.facci.inventario.enums.EnumCodigos;
import com.facci.inventario.enums.TipoArchivo;
import com.facci.inventario.handler.CustomException;
import com.facci.inventario.map.ArticuloMapper;
import com.facci.inventario.repositorio.ArticuloArchivoRepositorio;
import com.facci.inventario.repositorio.ArticuloAsignacionRepositorio;
import com.facci.inventario.repositorio.ArticuloHistorialRepositorio;
import com.facci.inventario.repositorio.ArticuloRepositorio;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;

import org.springframework.core.io.Resource;


import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Slf4j

@Service
public class ArchivoService {

    @Value("${articulo.carpeta-imagenes}")
    private String BASE_FOLDER;

    private final ArticuloArchivoRepositorio articuloArchivoRepositorio;
    private final ArticuloRepositorio articuloRepositorio;
    private final UsuarioSesionService usuarioSesionService;
    private final ArticuloMapper articuloMapper;
    private final ArticuloHistorialRepositorio articuloHistorialRepositorio;
    private final ArticuloAsignacionRepositorio articuloAsignacionRepositorio;
    private final ConfiguracionService configuracionService;

    public ArchivoService(ArticuloArchivoRepositorio articuloArchivoRepositorio, ArticuloRepositorio articuloRepositorio, UsuarioSesionService usuarioSesionService, ArticuloMapper articuloMapper, ArticuloHistorialRepositorio articuloHistorialRepositorio, ArticuloAsignacionRepositorio articuloAsignacionRepositorio, ConfiguracionService configuracionService) {
        this.articuloArchivoRepositorio = articuloArchivoRepositorio;
        this.articuloRepositorio = articuloRepositorio;
        this.usuarioSesionService = usuarioSesionService;
        this.articuloMapper = articuloMapper;
        this.articuloHistorialRepositorio = articuloHistorialRepositorio;
        this.articuloAsignacionRepositorio = articuloAsignacionRepositorio;
        this.configuracionService = configuracionService;
    }

    public String guardarImagen(Long idArticulo, MultipartFile file) {
        validarArchivoImagen(file);

        Articulo articulo = obtenerArticulo(idArticulo);

        try {
            Path carpetaArticulo = crearCarpetaArticulo(articulo.getCodigoInterno());
            String nombreImagen = generarNombreArchivo(articulo, calcularSecuencial(carpetaArticulo), file);
            Path rutaImagen = guardarArchivo(file, carpetaArticulo, nombreImagen);

            guardarRutaPath(articulo.getId(), TipoArchivo.IMAGEN, rutaImagen.toString());

            return rutaImagen.toString();
        } catch (IOException e) {
            throw new CustomException(EnumCodigos.IMAGEN_ERROR_GUARDAR);
        }
    }

    public String guardarPdf(Long articuloId, MultipartFile file) {
        validarArchivoPdf(file);

        Articulo articulo = obtenerArticulo(articuloId);

        try {
            Path carpetaArticulo = crearCarpetaArticulo(articulo.getCodigoInterno());
            String nombrePdf = limpiarNombreArchivo(file.getOriginalFilename());
            Path rutaPdf = guardarArchivo(file, carpetaArticulo, nombrePdf);

            guardarRutaPath(articulo.getId(), TipoArchivo.PDF, rutaPdf.toString());

            return rutaPdf.toString();
        } catch (IOException e) {
            throw new CustomException(EnumCodigos.PDF_ERROR_GUARDAR);
        }
    }

    public Resource obtenerArchivo(String path) {
        return articuloArchivoRepositorio.findByPath(path)
                .map(this::cargarArchivo)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARCHIVO_NO_ENCONTRADO));
    }

    private void validarArchivoImagen(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(EnumCodigos.IMAGEN_ARCHIVO_VACIO);
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new CustomException(EnumCodigos.IMAGEN_NO_VALIDA);
        }
    }

    private void validarArchivoPdf(MultipartFile file) {
        if (file.isEmpty() || !"application/pdf".equals(file.getContentType())) {
            throw new CustomException(EnumCodigos.PDF_NO_VALIDO);
        }
    }

    private Articulo obtenerArticulo(Long idArticulo) {
        return articuloRepositorio.findById(idArticulo)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
    }

    private Path crearCarpetaArticulo(String codigoInterno) throws IOException {
        Path carpetaArticulo = Paths.get(BASE_FOLDER, codigoInterno);
        if (!Files.exists(carpetaArticulo)) {
            Files.createDirectories(carpetaArticulo);
        }
        return carpetaArticulo;
    }

    private String generarNombreArchivo(Articulo articulo, int secuencial, MultipartFile file) {
        return String.format("%s_%s_%d%s",
                articulo.getCodigoInterno(),
                articulo.getCodigoOrigen(),
                secuencial,
                getExtension(file));
    }

    private String limpiarNombreArchivo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            throw new CustomException(EnumCodigos.PDF_NO_VALIDO);
        }
        return nombreArchivo.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private Path guardarArchivo(MultipartFile file, Path carpeta, String nombreArchivo) throws IOException {
        Path rutaArchivo = carpeta.resolve(nombreArchivo);
        Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
        return rutaArchivo;
    }

    private Resource cargarArchivo(ArticuloArchivo archivo) {
        try {
            Path ruta = Paths.get(archivo.getPath());
            if (!Files.exists(ruta) || !Files.isReadable(ruta)) {
                throw new CustomException(EnumCodigos.ARCHIVO_NO_ENCONTRADO);
            }
            return new UrlResource(ruta.toUri());
        } catch (MalformedURLException e) {
            throw new CustomException(EnumCodigos.ARCHIVO_NO_ENCONTRADO);
        }
    }

    private int calcularSecuencial(Path carpetaArticulo) throws IOException {
        try (Stream<Path> archivos = Files.list(carpetaArticulo)) {
            return archivos.map(path -> path.getFileName().toString())
                    .filter(nombre -> nombre.matches(".*_(\\d+)\\..*"))
                    .map(nombre -> nombre.replaceAll(".*_(\\d+)\\..*", "$1"))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0) + 1;
        }
    }

    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("El archivo no tiene extensión válida.");
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private void guardarRutaPath(long articuloId, TipoArchivo tipoArchivo, String path) {
        ArticuloArchivo archivo = new ArticuloArchivo();
        archivo.setArticuloId(articuloId);
        archivo.setTipo(tipoArchivo);
        archivo.setPath(path);
        articuloArchivoRepositorio.save(archivo);
    }


    public byte[] generarCodigoBarra(Long articuloId) {
        Articulo articulo = articuloRepositorio.findById(articuloId)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
        String codigoInterno = articulo.getCodigoInterno();

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    codigoInterno,
                    BarcodeFormat.CODE_128,
                    300,
                    100
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            log.info("Error al generar Codigo de barras", e);
            throw new CustomException(EnumCodigos.CODIGO_BARRAS_GENERAR);
        }
    }

    public byte[] generarReporteCodigosBarra(List<Long> articuloIds) {
        List<Map<String, Object>> datos = prepararDatosParaReporte(articuloIds);

        try {
            InputStream jasperStream = getClass().getResourceAsStream("/reportes/codigo_barras.jasper");
            if (jasperStream == null) {
                throw new CustomException(EnumCodigos.REPORTE_NO_ENCONTRADO);
            }
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JRDataSource dataSource = new JRBeanCollectionDataSource(datos);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);

            return pdfStream.toByteArray();
        } catch (JRException e) {
            log.error("Error al generar el reporte de códigos de barras", e);
            throw new CustomException(EnumCodigos.REPORTE_ERROR_GENERAR);
        }
    }

    public List<Map<String, Object>> prepararDatosParaReporte(List<Long> articuloIds) {
        List<Map<String, Object>> datos = new ArrayList<>();

        for (Long articuloId : articuloIds) {
            Articulo articulo = articuloRepositorio.findById(articuloId)
                    .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
            String codigoInterno = articulo.getCodigoInterno();
            try {
                InputStream codigoBarraStream = generarCodigoBarraReporte(articuloId);
                Map<String, Object> item = new HashMap<>();
                item.put("codigoInterno", codigoInterno);
                item.put("codigoBarra", codigoBarraStream);
                datos.add(item);
            } catch (Exception e) {
                log.error("Error al generar el código de barras para el artículo con ID {}", articuloId, e);
                throw new CustomException(EnumCodigos.CODIGO_BARRAS_GENERAR);
            }
        }

        return datos;
    }



    public byte[] generarReporteArticuloCompleto(Long articuloId) {
        byte[] reportePDF = generarReporteArticulo(articuloId);
        List<InputStream> pdfsAdicionales = cargarPDFs(articuloId);
        if (!pdfsAdicionales.isEmpty()) {
            return combinarTodosLosPDFs(reportePDF, pdfsAdicionales);
        }
        return reportePDF;
    }

    public byte[] combinarTodosLosPDFs(byte[] reportePDF, List<InputStream> pdfsAdicionales) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            PdfMerger merger = new PdfMerger(pdfDoc);

            PdfDocument reporteDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(reportePDF)));
            merger.merge(reporteDoc, 1, reporteDoc.getNumberOfPages());
            reporteDoc.close();

            for (InputStream pdfStream : pdfsAdicionales) {
                PdfDocument additionalDoc = new PdfDocument(new PdfReader(pdfStream));
                merger.merge(additionalDoc, 1, additionalDoc.getNumberOfPages());
                additionalDoc.close();
            }

            pdfDoc.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error al combinar los PDFs", e);
            throw new CustomException(EnumCodigos.ERROR_COMBINAR_PDFS);
        }
    }

    public byte[] generarReporteArticulo(Long articuloId) {
        ArticuloDetalleDTO detalleDTO = obtenerDetalleArticulo(articuloId);
        List<Map<String, Object>> historialData = obtenerHistorialData(articuloId);

        List<Map<String, Object>> archivosData = cargarArchivos(articuloId, TipoArchivo.IMAGEN);

        InputStream logoStream = obtenerLogoStream();
        InputStream codigoBarraStream = generarCodigoBarraReporte(articuloId);

        List<Map<String, Object>> datos = crearDatosPrincipales(detalleDTO, logoStream, codigoBarraStream);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("historial", new JRBeanCollectionDataSource(historialData));
        parameters.put("imagenes", new JRBeanCollectionDataSource(archivosData));

        return generarPDFReporte("/reportes/ReporteHistorial.jasper", parameters, datos);
    }

    private byte[] generarPDFReporte(String reportePath, Map<String, Object> parameters, List<Map<String, Object>> datos) {
        try (InputStream inputStream = getClass().getResourceAsStream(reportePath);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            JRDataSource dataSource = new JRBeanCollectionDataSource(datos);
            JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parameters, dataSource);

            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            return outputStream.toByteArray();
        } catch (JRException | IOException e) {
            log.error("Error al generar el reporte del artículo", e);
            throw new CustomException(EnumCodigos.REPORTE_ERROR_GENERAR);
        }
    }

    private ArticuloDetalleDTO obtenerDetalleArticulo(Long articuloId) {
        ArticuloDetalleDTO detalleDTO = new ArticuloDetalleDTO();
        Articulo articulo = articuloRepositorio.findById(articuloId)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
        ArticuloDTO articuloDTO = articuloMapper.mapToDto(articulo);


        articuloAsignacionRepositorio.findByArticuloId(articuloId).ifPresent(asignacion -> {
            UsuarioDTO usuarioDTO = configuracionService.consultarUsuario(asignacion.getIdUsuario());
            detalleDTO.setUsuarioAsignado(usuarioDTO);
        });

        detalleDTO.setArticulo(articuloDTO);

        return detalleDTO;
    }

    private List<Map<String, Object>> obtenerHistorialData(Long articuloId) {
        return articuloHistorialRepositorio.findByIdArticulo(articuloId).stream()
                .map(historialEntity -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("tipo", historialEntity.getTipoOperacion().toString());
                    map.put("descripcion", historialEntity.getDescripcion());
                    map.put("fecha", Date.from(historialEntity.getCreadoFecha().atZone(ZoneId.systemDefault()).toInstant()));
                    return map;
                }).collect(Collectors.toList());
    }

    private InputStream obtenerLogoStream() {
        return getClass().getResourceAsStream("/logoUleam.png");
    }

    private List<Map<String, Object>> crearDatosPrincipales(ArticuloDetalleDTO detalleDTO, InputStream logoStream, InputStream codigoBarraStream) {
        ArticuloDTO articuloDTO = detalleDTO.getArticulo();
        UsuarioDTO usuarioDTO = detalleDTO.getUsuarioAsignado();

        List<Map<String, Object>> datos = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("codigoBarra", codigoBarraStream);
        item.put("codigoInterno", articuloDTO.getCodigoInterno());
        item.put("codigoOrigen", articuloDTO.getCodigoOrigen());
        item.put("usuarioAsignado", usuarioDTO != null && usuarioDTO.getNombreCompleto() != null ? usuarioDTO.getNombreCompleto() : "");
        item.put("nombreArticulo", articuloDTO.getNombre());
        item.put("logo", logoStream);
        datos.add(item);

        return datos;
    }

    private List<Map<String, Object>> crearDatosPrincipalesActaEntrega(ArticuloDetalleDTO detalleDTO, InputStream logoStream, InputStream codigoBarraStream) {
        ArticuloDTO articuloDTO = detalleDTO.getArticulo();

        var usuarioAsigna = usuarioSesionService.usuarioCompleto();
        UsuarioDTO usuarioDTO = detalleDTO.getUsuarioAsignado();

        List<Map<String, Object>> datos = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("codigoBarra", codigoBarraStream);
        item.put("codigoInterno", articuloDTO.getCodigoInterno());
        item.put("codigoOrigen", articuloDTO.getCodigoOrigen());
        item.put("usuarioAsignado", usuarioDTO.getNombreCompleto());
        item.put("usuarioEntrega", usuarioAsigna.getNombreCompleto());
        item.put("nombreArticulo", articuloDTO.getNombre());
        item.put("logo", logoStream);
        datos.add(item);

        return datos;
    }
    public List<Map<String, Object>> cargarArchivos(Long articuloId, TipoArchivo tipoArchivo) {
        return articuloArchivoRepositorio.findByArticuloIdAndTipo(articuloId, tipoArchivo).stream()
                .map(archivo -> {
                    Map<String, Object> map = new HashMap<>();
                    try {
                        File imagenFile = new File(archivo.getPath());
                        if (imagenFile.exists()) {
                            InputStream imagenStream = new FileInputStream(imagenFile);
                            map.put("imagen", imagenStream);
                        } else {
                            throw new CustomException(EnumCodigos.ARCHIVO_NO_ENCONTRADO);
                        }
                    } catch (IOException e) {
                        log.error("No se encontró la imagen en el path: {}", archivo.getPath(), e);
                        throw new CustomException(EnumCodigos.ARCHIVO_NO_ENCONTRADO);
                    }
                    return map;
                }).collect(Collectors.toList());
    }

    public List<InputStream> cargarPDFs(Long articuloId) {
        return articuloArchivoRepositorio.findByArticuloIdAndTipo(articuloId, TipoArchivo.PDF).stream()
                .map(archivo -> {
                    File pdfFile = new File(archivo.getPath());
                    if (pdfFile.exists()) {
                        try {
                            return new FileInputStream(pdfFile);
                        } catch (FileNotFoundException e) {
                            log.error("No se encontró el archivo PDF en el path: {}", archivo.getPath(), e);
                            throw new CustomException(EnumCodigos.ARCHIVO_NO_ENCONTRADO);
                        }
                    } else {
                        throw new CustomException(EnumCodigos.ARCHIVO_NO_ENCONTRADO);
                    }
                }).collect(Collectors.toList());
    }

    public InputStream generarCodigoBarraReporte(Long articuloId) {
        Articulo articulo = articuloRepositorio.findById(articuloId)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
        String codigoInterno = articulo.getCodigoInterno();

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    codigoInterno,
                    BarcodeFormat.CODE_128,
                    300,
                    100
            );
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            log.error("Error al generar el código de barras", e);
            throw new CustomException(EnumCodigos.CODIGO_BARRAS_GENERAR);
        }
    }


    public byte[] generarReporteActaEntrega(Long articuloId) {
        ArticuloDetalleDTO detalleDTO = obtenerDetalleArticulo(articuloId);

        InputStream logoStream = obtenerLogoStream();
        InputStream codigoBarraStream = generarCodigoBarraReporte(articuloId);

        List<Map<String, Object>> datos = crearDatosPrincipalesActaEntrega(detalleDTO, logoStream, codigoBarraStream);

        Map<String, Object> parameters = new HashMap<>();


        return generarPDFReporte("/reportes/ReporteActaEntrega.jasper", parameters, datos);
    }
}
