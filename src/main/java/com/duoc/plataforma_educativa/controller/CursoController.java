package com.duoc.plataforma_educativa.controller;

import com.duoc.plataforma_educativa.model.Curso;
import com.duoc.plataforma_educativa.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de cursos.
 * Expone los endpoints:
 *   GET  /cursos  → lista todos los cursos disponibles
 *   POST /cursos  → agrega un nuevo curso a la oferta educativa
 */
@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    /**
     * GET /cursos
     * Retorna la lista completa de cursos con nombre, instructor, duración y costo.
     * Si no hay cursos registrados, retorna lista vacía con HTTP 200.
     */
    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        List<Curso> cursos = cursoService.listarCursos();
        return ResponseEntity.ok(cursos);
    }

    /**
     * POST /cursos
     * Agrega un nuevo curso a la oferta educativa y lo persiste en Oracle Cloud.
     * Body esperado (JSON):
     * {
     *   "nombre":     "Spring Boot Avanzado",
     *   "instructor": "Carlos Valverde",
     *   "duracion":   40,
     *   "costo":      150000
     * }
     */
    @PostMapping
    public ResponseEntity<?> agregarCurso(@RequestBody Curso curso) {
        try {
            Curso nuevo = cursoService.agregarCurso(curso);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
