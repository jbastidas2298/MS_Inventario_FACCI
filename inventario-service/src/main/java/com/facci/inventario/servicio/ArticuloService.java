package com.facci.inventario.servicio;

import com.facci.inventario.Configuracion.ConfiguracionService;
import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloAsignacion;
import com.facci.inventario.dto.*;
import com.facci.inventario.enums.*;
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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ArticuloService {

    private final ArticuloRepositorio articuloRepositorio;
    private final ArticuloMapper articuloMapper;
    private final SecuencialService secuencialService;
    private final ArticuloHistorialService articuloHistorialService;
    private final ConfiguracionService configuracionService;
    private final ArticuloAsignacionService articuloAsignacionService;
    private final UsuarioSesionService usuarioSesionService;
    private final ArticuloAsignacionRepositorio articuloAsignacionRepositorio;
    private final ArticuloArchivoRepositorio articuloArchivoRepositorio;
    private final ArticuloHistorialRepositorio articuloHistorialRepositorio;

    public ArticuloService(ArticuloRepositorio articuloRepositorio, ArticuloMapper articuloMapper, SecuencialService secuencialService, ArticuloHistorialService articuloHistorialService, ConfiguracionService configuracionService, ArticuloAsignacionService articuloAsignacionService, UsuarioSesionService usuarioSesionService, ArticuloAsignacionRepositorio articuloAsignacionRepositorio, ArticuloArchivoRepositorio articuloArchivoRepositorio, ArticuloHistorialRepositorio articuloHistorialRepositorio) {
        this.articuloRepositorio = articuloRepositorio;
        this.articuloMapper = articuloMapper;
        this.secuencialService = secuencialService;
        this.articuloHistorialService = articuloHistorialService;
        this.configuracionService = configuracionService;
        this.articuloAsignacionService = articuloAsignacionService;
        this.usuarioSesionService = usuarioSesionService;
        this.articuloAsignacionRepositorio = articuloAsignacionRepositorio;
        this.articuloArchivoRepositorio = articuloArchivoRepositorio;
        this.articuloHistorialRepositorio = articuloHistorialRepositorio;
    }

    @Transactional
    public ArticuloDTO registrar(ArticuloDTO dto) {
        Optional<Articulo> articuloExistente = articuloRepositorio.findByCodigoOrigen(dto.getCodigoOrigen());
        if (articuloExistente.isPresent()) {
            log.error("El artículo con código de origen '{}' ya existe.", dto.getCodigoOrigen());
            throw new CustomException(EnumCodigos.ARTICULO_YA_EXISTE);
        }

        String secuencial = secuencialService.generarSecuencial("Articulo");
        dto.setCodigoInterno(secuencial);
        Articulo nuevoArticulo = new Articulo(dto);
        Articulo articuloGuardado = articuloRepositorio.save(nuevoArticulo);

        String usuario = usuarioSesionService.obtenerUsuarioActual()
                .orElseThrow(() -> new CustomException(EnumCodigos.USUARIO_ASIGNAR_EN_SESION));

        UsuarioDTO usuarioSesion = configuracionService.buscarPorNombreUsuario(usuario);
        if (usuarioSesion == null) {
            log.error("No se encontró información del usuario en el servicio de configuración para '{}'.", usuario);
            throw new CustomException(EnumCodigos.USUARIO_ASIGNAR_EN_SESION);
        }
        articuloHistorialService.registrarEvento(articuloGuardado, TipoOperacion.INGRESO, TipoOperacion.INGRESO + " " + articuloGuardado.getObservacion(),usuarioSesion);
        articuloAsignacionService.asignarArticulos(
                usuarioSesion.getId(),
                TipoRelacion.USUARIO,
                Collections.singletonList(articuloGuardado.getId())
        );
        log.debug("Artículo registrado: {}", articuloGuardado.getNombre());

        return articuloMapper.mapToDto(articuloGuardado);
    }

    public ArticuloDTO actualizar(ArticuloDTO articuloDTO) {
        Articulo articuloExistente = articuloRepositorio.findById(articuloDTO.getId())
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));

        articuloExistente.setNombre(articuloDTO.getNombre());
        articuloExistente.setDescripcion(articuloDTO.getDescripcion());
        articuloExistente.setEstado(articuloDTO.getEstado());
        articuloExistente.setMarca(articuloDTO.getMarca());
        articuloExistente.setObservacion(articuloDTO.getObservacion());
        Articulo articuloActualizado = articuloRepositorio.save(articuloExistente);
        UsuarioDTO usuarioDTO = usuarioSesionService.usuarioCompleto();
        articuloHistorialService.registrarEvento(articuloActualizado, TipoOperacion.ACTUALIZACION, null,usuarioDTO);

        log.info("Artículo actualizado por usuario: {}", articuloActualizado.getNombre());
        return articuloMapper.mapToDto(articuloActualizado);
    }

    public void eliminar(Long id) {
        Articulo articulo = articuloRepositorio.findById(id)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));

        Optional<ArticuloAsignacion> articuloAsignacion = articuloAsignacionRepositorio.findByArticuloId(id);
        if (!articuloAsignacion.isEmpty()) {
            throw new CustomException(EnumCodigos.ARTICULO_ASIGNADO_NO_ELIMINABLE);
        }
        articuloRepositorio.delete(articulo);
        log.info("Artículo eliminado con id: {}", id);
    }

    public ArticuloDTO consultarArticulo(long id) {
        Articulo articulo = articuloRepositorio.findById(id)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
        return articuloMapper.mapToDto(articulo);
    }

    public List<ArticuloDTO> consultarTodos() {
        List<EnumRolUsuario> roles = usuarioSesionService.obtenerRolesActuales();

        if (roles.contains(EnumRolUsuario.ADMINISTRADOR)) {
            List<Articulo> articulos = StreamSupport.stream(articuloRepositorio.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            log.info("Artículos consultados (ADMINISTRADOR): {}", articulos.size());
            return articulos.stream()
                    .map(articuloMapper::mapToDto)
                    .collect(Collectors.toList());
        } else {
            log.info("Consulta restringida para roles: {}", roles);
            List<Long> idsArticulosAsignados = articuloAsignacionService.obtenerIdsArticulosAsignadosAlUsuario();
            List<Articulo> articulos = StreamSupport.stream(articuloRepositorio.findAllById(idsArticulosAsignados).spliterator(), false)
                    .collect(Collectors.toList());
            log.info("Artículos consultados para el usuario en sesión: {}", articulos.size());

            return articulos.stream()
                    .map(articuloMapper::mapToDto)
                    .collect(Collectors.toList());
        }
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

    public ArticuloDetalleDTO obtenerArticuloDetalle(Long articuloId) {
        ArticuloDetalleDTO detalleDTO = new ArticuloDetalleDTO();

        Articulo articulo = articuloRepositorio.findById(articuloId)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
        ArticuloDTO articuloDTO = articuloMapper.mapToDto(articulo);
        detalleDTO.setArticulo(articuloDTO);

        List<ArticuloArchivoDTO> archivos = articuloArchivoRepositorio.findByArticuloId(articuloId).stream()
                .map(archivo -> {
                    ArticuloArchivoDTO archivoDTO = new ArticuloArchivoDTO();
                    archivoDTO.setPath(archivo.getPath());
                    archivoDTO.setTipo(archivo.getTipo());
                    return archivoDTO;
                }).collect(Collectors.toList());
        detalleDTO.setArchivos(archivos);

        List<ArticuloHistorialDTO> historial = articuloHistorialRepositorio.findByIdArticulo(articuloId).stream()
                .map(historialEntity -> {
                    ArticuloHistorialDTO historialDTO = new ArticuloHistorialDTO();
                    historialDTO.setCodigoInterno(historialEntity.getCodigoInterno());
                    historialDTO.setTipoOperacion(historialEntity.getTipoOperacion());
                    historialDTO.setDescripcion(historialEntity.getDescripcion());
                    return historialDTO;
                }).collect(Collectors.toList());
        detalleDTO.setHistorial(historial);

        ArticuloAsignacion asignacion = articuloAsignacionRepositorio.findByArticuloId(articuloId)
                .orElseThrow(() -> new CustomException(EnumCodigos.ASIGNACIONES_NO_ENCONTRADAS));

        UsuarioDTO usuarioDTO = configuracionService.consultarUsuario(asignacion.getIdUsuario());
        detalleDTO.setUsuarioAsignado(usuarioDTO);

        return detalleDTO;
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
        Articulo articulo = articuloRepositorio.findById(articuloId)
                .orElseThrow(() -> new CustomException(EnumCodigos.ARTICULO_NO_ENCONTRADO));
        ArticuloDTO articuloDTO = articuloMapper.mapToDto(articulo);

        ArticuloAsignacion asignacion = articuloAsignacionRepositorio.findByArticuloId(articuloId)
                .orElseThrow(() -> new CustomException(EnumCodigos.ASIGNACIONES_NO_ENCONTRADAS));
        UsuarioDTO usuarioDTO = configuracionService.consultarUsuario(asignacion.getIdUsuario());

        ArticuloDetalleDTO detalleDTO = new ArticuloDetalleDTO();
        detalleDTO.setArticulo(articuloDTO);
        detalleDTO.setUsuarioAsignado(usuarioDTO);

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
        item.put("usuarioAsignado", usuarioDTO.getNombreCompleto());
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
