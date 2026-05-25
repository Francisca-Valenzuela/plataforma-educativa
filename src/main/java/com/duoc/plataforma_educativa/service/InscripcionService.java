package com.duoc.plataforma_educativa.service;

import com.duoc.plataforma_educativa.model.Curso;
import com.duoc.plataforma_educativa.model.Inscripcion;
import com.duoc.plataforma_educativa.repository.CursoRepository;
import com.duoc.plataforma_educativa.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Crea una inscripción para un estudiante.
     * Recibe el nombre del estudiante y una lista de IDs de cursos.
     * Calcula el total a pagar sumando el costo de cada curso.
     * Persiste la inscripción en Oracle Cloud y retorna el resumen completo.
     */
    public Inscripcion inscribir(String nombreEstudiante, List<Long> cursoIds) {

        if (nombreEstudiante == null || nombreEstudiante.isBlank()) {
            throw new IllegalArgumentException("El nombre del estudiante es obligatorio.");
        }
        if (cursoIds == null || cursoIds.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un curso.");
        }

        // Buscar los cursos en BD; valida que todos existan
        List<Curso> cursos = cursoRepository.findAllById(cursoIds);
        if (cursos.size() != cursoIds.size()) {
            throw new IllegalArgumentException("Uno o más IDs de curso no existen.");
        }

        // Calcular total sumando el costo de cada curso seleccionado
        double total = cursos.stream()
                .mapToDouble(Curso::getCosto)
                .sum();

        Inscripcion inscripcion = new Inscripcion(nombreEstudiante, cursos, total);
        return inscripcionRepository.save(inscripcion);
    }
}
