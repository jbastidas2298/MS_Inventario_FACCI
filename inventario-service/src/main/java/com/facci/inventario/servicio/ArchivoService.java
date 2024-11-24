package com.facci.inventario.servicio;

import com.facci.inventario.dominio.Articulo;
import com.facci.inventario.dominio.ArticuloArchivo;
import com.facci.inventario.enums.EnumErrores;
import com.facci.inventario.enums.TipoArchivo;
import com.facci.inventario.handler.CustomException;
import com.facci.inventario.repositorio.ArticuloArchivoRepositorio;
import com.facci.inventario.repositorio.ArticuloRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;

import org.springframework.core.io.Resource;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;
@Slf4j

@Service
public class ArchivoService {
    @Value("${articulo.carpeta-imagenes}")
    private String BASE_FOLDER;

    private final ArticuloArchivoRepositorio articuloArchivoRepositorio;
    private final ArticuloRepositorio articuloRepositorio;

    public ArchivoService(ArticuloArchivoRepositorio articuloArchivoRepositorio, ArticuloRepositorio articuloRepositorio){

        this.articuloArchivoRepositorio = articuloArchivoRepositorio;
        this.articuloRepositorio = articuloRepositorio;
    }
    public String guardarImagen(Long idArticulo, MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(EnumErrores.IMAGEN_ARCHIVO_VACIO);
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new CustomException(EnumErrores.IMAGEN_NO_VALIDA);
        }

        Articulo articulo = articuloRepositorio.findById(idArticulo)
                .orElseThrow(() -> new CustomException(EnumErrores.ARTICULO_NO_ENCONTRADO));

        try {
            Path carpetaArticulo = Paths.get(BASE_FOLDER, articulo.getCodigoInterno());
            if (!Files.exists(carpetaArticulo)) {
                Files.createDirectories(carpetaArticulo);
            }
            int siguienteSecuencial = calcularSecuencial(carpetaArticulo);
            String nombreImagen = String.format("%s_%s_%d%s",
                    articulo.getCodigoInterno(),
                    articulo.getCodigoOrigen(),
                    siguienteSecuencial,
                    getExtension(file)
            );
            Path rutaImagen = carpetaArticulo.resolve(nombreImagen);

            Files.copy(file.getInputStream(), rutaImagen, StandardCopyOption.REPLACE_EXISTING);

            guardarRutaPath(articulo.getId(),TipoArchivo.IMAGEN,rutaImagen.toString());

            return rutaImagen.toString();
        } catch (IOException e) {
            throw new CustomException(EnumErrores.IMAGEN_ERROR_GUARDAR);
        }
    }

    public String guardarPdf(Long articuloId, MultipartFile file) {
        if (file.isEmpty() || !Objects.equals(file.getContentType(), "application/pdf")) {
            log.error("El archivo proporcionado no es un PDF válido.");
            throw new CustomException(EnumErrores.PDF_NO_VALIDO);
        }
        Articulo articulo = articuloRepositorio.findById(articuloId)
                .orElseThrow(() -> {
                    log.error("Artículo con ID {} no encontrado.", articuloId);
                    return new CustomException(EnumErrores.ARTICULO_NO_ENCONTRADO);
                });

        try {
            String folderPath = Paths.get(BASE_FOLDER, articulo.getCodigoInterno()).toString();
            File folder = new File(folderPath);
            if (!folder.exists() && !folder.mkdirs()) {
                log.error("No se pudo crear la carpeta para el artículo con ID {}.", articuloId);
                throw new CustomException(EnumErrores.CARPETA_NO_CREADA);
            }

            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isEmpty()) {
                log.error("El archivo proporcionado no tiene un nombre válido.");
                throw new CustomException(EnumErrores.PDF_NO_VALIDO);
            }

            String cleanedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");

            Path pdfFilePath = Paths.get(folderPath, cleanedFileName);


            Files.copy(file.getInputStream(), pdfFilePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("PDF guardado exitosamente en la ruta: {}", pdfFilePath);
            guardarRutaPath(articulo.getId(),TipoArchivo.PDF,pdfFilePath.toString());

            return pdfFilePath.toString();
        } catch (IOException e) {
            log.error("Error al guardar el PDF para el artículo con ID {}.", articuloId, e);
            throw new CustomException(EnumErrores.PDF_ERROR_GUARDAR);
        }
    }

    public Resource obtenerArchivo(String path) {
        return articuloArchivoRepositorio.findByPath(path)
                .map(archivo -> {
                    try {
                        Path filePath = Paths.get(archivo.getPath());
                        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                            throw new CustomException(EnumErrores.ARCHIVO_NO_ENCONTRADO);
                        }
                        return new UrlResource(filePath.toUri());
                    } catch (MalformedURLException e) {
                        log.error("Error al construir la URL del recurso para el archivo en la ruta: {}", path, e);
                        throw new CustomException(EnumErrores.ARCHIVO_NO_ENCONTRADO);
                    }
                })
                .orElseThrow(() -> {
                    log.error("Archivo no encontrado en la base de datos para la ruta: {}", path);
                    return new CustomException(EnumErrores.ARCHIVO_NO_ENCONTRADO);
                });
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

    private void guardarRutaPath(long articuloId, TipoArchivo tipoArchivo,String Path){
        ArticuloArchivo archivo = new ArticuloArchivo();
        archivo.setArticuloId(articuloId);
        archivo.setTipo(tipoArchivo);
        archivo.setPath(Path);
        articuloArchivoRepositorio.save(archivo);
    }



}
