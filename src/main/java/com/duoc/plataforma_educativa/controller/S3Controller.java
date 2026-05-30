package com.duoc.plataforma_educativa.controller;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duoc.plataforma_educativa.service.S3Service;

import software.amazon.awssdk.services.s3.model.S3Exception;
import java.util.List;

/**
 * Controlador REST para operaciones de almacenamiento en AWS S3.
 *
 * Endpoints expuestos:
 * POST /inscripciones/{id}/resumen/subir → sube el resumen a S3
 * GET /inscripciones/{id}/resumen/descargar → descarga el resumen desde S3
 * PUT /inscripciones/{id}/resumen/reemplazar → reemplaza el resumen en S3
 * DELETE /inscripciones/{id}/resumen → elimina el resumen de S3
 */
@RestController
@RequestMapping("/inscripciones")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // ── POST /inscripciones/{id}/resumen/subir ─────────────────────────────────

    /**
     * Genera el resumen de la inscripción y lo sube al bucket S3.
     * La key en S3 tendrá el formato: resumen-{id}/resumen_inscripcion_{id}.txt
     */
    @PostMapping("/{id}/resumen/subir")
    public ResponseEntity<?> subirResumen(@PathVariable Long id) {
        try {
            String mensaje = s3Service.subirResumen(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el archivo a S3: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    // ── GET /inscripciones/{id}/resumen/descargar ──────────────────────────────

    /**
     * Descarga el archivo de resumen desde S3 y lo retorna como archivo
     * descargable.
     * Retorna 404 si el archivo no existe en el bucket.
     */
    @GetMapping("/{id}/resumen/descargar")
    public ResponseEntity<?> descargarResumen(@PathVariable Long id) {
        try {
            byte[] bytes = s3Service.descargarResumen(id);

            String filename = "resumen_inscripcion_" + id + ".txt";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al descargar el archivo desde S3: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    // ── PUT /inscripciones/{id}/resumen/reemplazar ─────────────────────────────

    /**
     * Recibe un archivo via MultipartFile y reemplaza el resumen existente en S3.
     * Usa la misma key para sobreescribir el objeto.
     * Body: form-data con campo "archivo" de tipo File.
     */
    @PutMapping("/{id}/resumen/reemplazar")
    public ResponseEntity<?> reemplazarResumen(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("El archivo enviado está vacío. Por favor adjunte un archivo válido.");
            }
            String mensaje = s3Service.reemplazarResumen(id, archivo);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se pudo leer el archivo enviado: " + e.getMessage());
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al reemplazar el archivo en S3: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    // ── DELETE /inscripciones/{id}/resumen ─────────────────────────────────────

    /**
     * Elimina el archivo de resumen de S3.
     * Retorna 404 si el archivo no existe en el bucket antes de intentar
     * eliminarlo.
     */
    @DeleteMapping("/{id}/resumen")
    public ResponseEntity<?> eliminarResumen(@PathVariable Long id) {
        try {
            String mensaje = s3Service.eliminarResumen(id);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            // El archivo no existe en S3
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el archivo de S3: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    // ── GET /inscripciones/{id}/resumen/local ─────────────────────────────────
    /**
     * Endpoint correspondiente a la Semana 1.
     * Genera un archivo físico temporal en el servidor y lo descarga directamente,
     * sin interactuar con AWS S3.
     */
    @GetMapping("/{id}/resumen/local")
    public ResponseEntity<?> descargarResumenLocal(@PathVariable Long id) {
        try {
            // 1. Llamamos a tu método que genera el archivo físico en el servidor
            Path rutaArchivo = s3Service.generarArchivoFisico(id);
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            String filename = "resumen_local_inscripcion_" + id + ".txt";

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(recurso);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo local: " + e.getMessage());
        }
    }

    // ── GET /inscripciones/resumen/listar ─────────────────────────────────────
    
    /**
     * Lista todos los objetos que existen actualmente en el bucket S3.
     */
    @GetMapping("/resumen/listar")
    public ResponseEntity<?> listarTodoElBucket() {
        try {
            List<String> archivos = s3Service.listarArchivosS3();
            return ResponseEntity.ok(archivos);
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al listar los archivos de S3: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

}