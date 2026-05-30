package com.duoc.plataforma_educativa.controller;

import com.duoc.plataforma_educativa.dto.InscripcionRequest;
import com.duoc.plataforma_educativa.model.Inscripcion;
import com.duoc.plataforma_educativa.service.InscripcionService;
import com.duoc.plataforma_educativa.service.S3Service;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controlador REST para inscripción de estudiantes.
 * Expone el endpoint:
 *  POST /inscripciones                          → inscribe un estudiante en uno o más cursos,  calcula el total y retorna el resumen/boleta.
 *  GET  /inscripciones/{id}/resumen/generar     → genera y descarga el resumen como .txt
 */
@RestController
@RequestMapping("/inscripciones")
public class InscripcionController {

    
    private InscripcionService inscripcionService;
    private final S3Service s3Service;

    public InscripcionController(InscripcionService inscripcionService,
                                 S3Service s3Service) {
        this.inscripcionService = inscripcionService;
        this.s3Service          = s3Service;
    }

    /**
     * POST /inscripciones
     * Inscribe a un estudiante en los cursos indicados.
     * Retorna un resumen (boleta) con:
     *   - ID de inscripción
     *   - Nombre del estudiante
     *   - Lista de cursos inscritos (nombre, instructor, duración, costo)
     *   - Total a pagar (suma de costos)
     *   - Fecha de inscripción
     *
     * Body esperado (JSON):
     * {
     *   "nombreEstudiante": "Ana García",
     *   "cursoIds": [1, 2, 3]
     * }
     */
    @PostMapping
    public ResponseEntity<?> inscribir(@RequestBody InscripcionRequest request) {
        try {
            Inscripcion inscripcion = inscripcionService.inscribir(
                    request.getNombreEstudiante(),
                    request.getCursoIds()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(inscripcion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /inscripciones/{id}/resumen/generar
     * Genera un archivo .txt físico con el resumen de la inscripción y lo retorna
     * como descarga directa (Content-Disposition: attachment).
     *
     * El archivo contiene: ID, nombre del estudiante, lista de cursos
     * (nombre, instructor, duración, costo), total a pagar y fecha de inscripción.
     */
    @GetMapping("/{id}/resumen/generar")
    public ResponseEntity<?> generarResumen(@PathVariable Long id) {
        Path tempFile = null;
        try {
            // Genera el archivo temporal en el servidor
            tempFile = s3Service.generarArchivoFisico(id);
            byte[] bytes = Files.readAllBytes(tempFile);

            String filename = "resumen_inscripcion_" + id + ".txt";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename(filename).build());
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el archivo de resumen: " + e.getMessage());
        } finally {
            // Limpieza: elimina el archivo temporal del servidor después de enviarlo
            if (tempFile != null) {
                try { Files.deleteIfExists(tempFile); } catch (IOException ignored) {}
            }
        }
    }
}

