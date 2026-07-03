package com.duoc.plataforma_educativa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.duoc.plataforma_educativa.config.RabbitMQConfig;
import com.duoc.plataforma_educativa.dto.ResumenInscripcionMensaje;

/**
 * Productor (Semana 7): envía el resumen de la inscripción hacia la cola
 * RabbitMQ ("inscripciones.queue") a través del exchange "inscripciones.exchange".
 *
 * Es invocado por InscripcionService cada vez que se crea una nueva
 * inscripción (POST /inscripciones).
 */
@Service
public class ProductorService {

    private static final Logger log = LoggerFactory.getLogger(ProductorService.class);

    private final AmqpTemplate rabbitTemplate;

    public ProductorService(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarResumenInscripcion(ResumenInscripcionMensaje mensaje) {
        log.info("Publicando en la cola '{}' el resumen de la inscripción {}",
                RabbitMQConfig.QUEUE_INSCRIPCIONES, mensaje.getInscripcionId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_INSCRIPCIONES,
                RabbitMQConfig.ROUTING_KEY_INSCRIPCIONES,
                mensaje
        );
    }
}