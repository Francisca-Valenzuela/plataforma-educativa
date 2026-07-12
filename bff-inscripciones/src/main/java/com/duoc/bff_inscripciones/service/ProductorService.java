package com.duoc.bff_inscripciones.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.duoc.bff_inscripciones.config.RabbitMQConfig;
import com.duoc.bff_inscripciones.dto.SolicitudInscripcionMensaje;

/**
 * Orquesta la publicación de solicitudes de inscripción hacia
 * "solicitud-inscripcion.queue", consumida por plataforma-educativa.
 */
@Service
public class ProductorService {

    private static final Logger log = LoggerFactory.getLogger(ProductorService.class);

    private final AmqpTemplate rabbitTemplate;

    public ProductorService(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarSolicitudInscripcion(SolicitudInscripcionMensaje solicitud) {
        log.info("Publicando solicitud de inscripción para el estudiante '{}'",
                solicitud.getNombreEstudiante());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_SOLICITUD_INSCRIPCION,
                RabbitMQConfig.ROUTING_KEY_SOLICITUD_INSCRIPCION,
                solicitud
        );
    }
}