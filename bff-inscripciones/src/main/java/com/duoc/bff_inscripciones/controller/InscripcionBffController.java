package com.duoc.bff_inscripciones.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.bff_inscripciones.dto.SolicitudInscripcionMensaje;
import com.duoc.bff_inscripciones.service.ProductorService;

/**
 * Punto de entrada del BFF para el frontend. Valida el JWT/rol (ver
 * SecurityConfig) y orquesta la solicitud hacia la cola RabbitMQ, sin
 * tocar la base de datos directamente.
 *
 * POST /bff/inscripciones
 * Body esperado:
 * {
 *   "nombreEstudiante": "Ana García",
 *   "cursoIds": [1]
 * }
 */
@RestController
@RequestMapping("/bff/inscripciones")
public class InscripcionBffController {

    private final ProductorService productorService;

    public InscripcionBffController(ProductorService productorService) {
        this.productorService = productorService;
    }

    @PostMapping
    public ResponseEntity<?> solicitarInscripcion(@RequestBody SolicitudInscripcionMensaje solicitud) {
        if (solicitud.getNombreEstudiante() == null || solicitud.getNombreEstudiante().isBlank()) {
            return ResponseEntity.badRequest().body("El nombre del estudiante es obligatorio.");
        }
        if (solicitud.getCursoIds() == null || solicitud.getCursoIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Debe seleccionar al menos un curso.");
        }

        productorService.enviarSolicitudInscripcion(solicitud);

        // 202 Accepted: la solicitud fue aceptada para procesamiento asíncrono,
        // no confirma persistencia inmediata (eso lo hace plataforma-educativa).
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Solicitud de inscripción recibida y en proceso.");
    }
}