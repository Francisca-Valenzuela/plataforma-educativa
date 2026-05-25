package com.duoc.plataforma_educativa.controller;

import com.duoc.plataforma_educativa.dto.InscripcionRequest;
import com.duoc.plataforma_educativa.model.Inscripcion;
import com.duoc.plataforma_educativa.service.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para inscripción de estudiantes.
 * Expone el endpoint:
 *   POST /inscripciones → inscribe un estudiante en uno o más cursos,
 *                         calcula el total y retorna el resumen/boleta.
 */
@RestController
@RequestMapping("/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

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
}
