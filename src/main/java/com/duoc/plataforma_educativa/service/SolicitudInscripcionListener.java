package com.duoc.plataforma_educativa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.duoc.plataforma_educativa.dto.SolicitudInscripcionMensaje;

/**
 * Escucha "solicitud-inscripcion.queue", publicada por el microservicio BFF
 * (bff-inscripciones), y delega en InscripcionService la creación real de
 * la inscripción (persistencia + publicación del resumen en
 * "inscripciones.queue", flujo ya existente).
 */
@Service
public class SolicitudInscripcionListener {

    private static final Logger log = LoggerFactory.getLogger(SolicitudInscripcionListener.class);

    private final InscripcionService inscripcionService;

    public SolicitudInscripcionListener(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @RabbitListener(queues = "solicitud-inscripcion.queue")
    public void recibirSolicitud(SolicitudInscripcionMensaje solicitud) {
        log.info("Solicitud de inscripción recibida desde el BFF para el estudiante '{}'",
                solicitud.getNombreEstudiante());
        try {
            inscripcionService.inscribir(solicitud.getNombreEstudiante(), solicitud.getCursoIds());
        } catch (IllegalArgumentException e) {
            log.error("No se pudo procesar la solicitud de inscripción: {}", e.getMessage());
        }
    }
}