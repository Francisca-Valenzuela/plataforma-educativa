package com.duoc.plataforma_educativa.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.duoc.plataforma_educativa.config.RabbitMQConfig;
import com.duoc.plataforma_educativa.dto.CursoItemDTO;
import com.duoc.plataforma_educativa.dto.ResumenInscripcionMensaje;
import com.duoc.plataforma_educativa.model.ResumenCompra;
import com.duoc.plataforma_educativa.repository.ResumenCompraRepository;

/**
 * Consumidor (Semana 7): escucha la cola "inscripciones.queue" y, por cada
 * mensaje recibido, guarda el resumen de compra en la nueva tabla
 * RESUMEN_COMPRA de la base de datos.
 *
 * Este es el "endpoint" de consumo: no es un endpoint
 * REST, sino un listener AMQP.
 */
@Service
public class ConsumidorService {

    private static final Logger log = LoggerFactory.getLogger(ConsumidorService.class);

    private final ResumenCompraRepository resumenCompraRepository;

    public ConsumidorService(ResumenCompraRepository resumenCompraRepository) {
        this.resumenCompraRepository = resumenCompraRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_INSCRIPCIONES)
    public void recibirResumenInscripcion(ResumenInscripcionMensaje mensaje) {
        log.info("Mensaje recibido desde la cola '{}': {}",
                RabbitMQConfig.QUEUE_INSCRIPCIONES, mensaje);

        String detalleCursos = formatearDetalleCursos(mensaje.getCursos());

        ResumenCompra resumen = new ResumenCompra(
                mensaje.getInscripcionId(),
                mensaje.getNombreEstudiante(),
                detalleCursos,
                mensaje.getTotalPagar(),
                mensaje.getFechaInscripcion()
        );

        ResumenCompra guardado = resumenCompraRepository.save(resumen);

        log.info("Resumen de compra guardado en la Base de datos (RESUMEN_COMPRA) con id {}",
                guardado.getId());
    }

    private String formatearDetalleCursos(List<CursoItemDTO> cursos) {
        if (cursos == null || cursos.isEmpty()) {
            return "";
        }
        return cursos.stream()
                .map(c -> String.format("%s (Instructor: %s, %dh, $%.2f)",
                        c.getNombre(), c.getInstructor(), c.getDuracion(), c.getCosto()))
                .collect(Collectors.joining("; "));
    }
}