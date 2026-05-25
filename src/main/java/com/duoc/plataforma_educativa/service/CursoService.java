package com.duoc.plataforma_educativa.service;

import com.duoc.plataforma_educativa.model.Curso;
import com.duoc.plataforma_educativa.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Retorna la lista completa de cursos disponibles.
     */
    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    /**
     * Persiste un nuevo curso en la base de datos Oracle Cloud.
     * Valida que nombre, instructor, duración y costo no sean nulos.
     */
    public Curso agregarCurso(Curso curso) {
        if (curso.getNombre() == null || curso.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio.");
        }
        if (curso.getInstructor() == null || curso.getInstructor().isBlank()) {
            throw new IllegalArgumentException("El instructor del curso es obligatorio.");
        }
        if (curso.getDuracion() == null || curso.getDuracion() <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 horas.");
        }
        if (curso.getCosto() == null || curso.getCosto() < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo.");
        }
        return cursoRepository.save(curso);
    }
}
