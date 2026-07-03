package com.duoc.plataforma_educativa.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoc.plataforma_educativa.dto.CursoItemDTO;
import com.duoc.plataforma_educativa.dto.ResumenInscripcionMensaje;
import com.duoc.plataforma_educativa.model.Curso;
import com.duoc.plataforma_educativa.model.Inscripcion;
import com.duoc.plataforma_educativa.repository.CursoRepository;
import com.duoc.plataforma_educativa.repository.InscripcionRepository;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private ProductorService productorService;

    /**
     * Crea una inscripción para un estudiante.
     * Recibe el nombre del estudiante y una lista de IDs de cursos.
     * Calcula el total a pagar sumando el costo de cada curso.
     * Persiste la inscripción y, además, publica el resumen en la cola
     * RabbitMQ (Semana 7) para que sea procesado de forma asíncrona por el
     * consumidor, quien lo guardará en la tabla RESUMEN_COMPRA de Oracle Cloud.
     */
    public Inscripcion inscribir(String nombreEstudiante, List<Long> cursoIds) {

        if (nombreEstudiante == null || nombreEstudiante.isBlank()) {
            throw new IllegalArgumentException("El nombre del estudiante es obligatorio.");
        }
        if (cursoIds == null || cursoIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un curso.");
        }

        List<Curso> cursos = cursoRepository.findAllById(cursoIds);
        if (cursos.size() != cursoIds.size()) {
            throw new IllegalArgumentException("Uno o más IDs de curso no existen.");
        }

        double total = cursos.stream()
                .mapToDouble(Curso::getCosto)
                .sum();

        Inscripcion inscripcion = new Inscripcion(nombreEstudiante, cursos, total);
        Inscripcion inscripcionGuardada = inscripcionRepository.save(inscripcion);

        // Semana 7: enviar el resumen de la inscripción a la cola MQ para su
        // procesamiento asíncrono por el consumidor.
        enviarResumenACola(inscripcionGuardada, cursos, total);

        return inscripcionGuardada;
    }

    private void enviarResumenACola(Inscripcion inscripcion, List<Curso> cursos, double total) {
        List<CursoItemDTO> cursosDTO = cursos.stream()
                .map(c -> new CursoItemDTO(c.getNombre(), c.getInstructor(), c.getDuracion(), c.getCosto()))
                .collect(Collectors.toList());

        ResumenInscripcionMensaje mensaje = new ResumenInscripcionMensaje(
                inscripcion.getId(),
                inscripcion.getNombreEstudiante(),
                cursosDTO,
                total,
                inscripcion.getFechaInscripcion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        productorService.enviarResumenInscripcion(mensaje);
    }
}