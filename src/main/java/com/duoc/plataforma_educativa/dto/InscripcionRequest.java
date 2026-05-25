package com.duoc.plataforma_educativa.dto;

import java.util.List;

/**
 * DTO que representa el cuerpo del request para crear una inscripción.
 * Recibe el nombre del estudiante y la lista de IDs de los cursos a inscribir.
 */
public class InscripcionRequest {

    private String nombreEstudiante;
    private List<Long> cursoIds;

    public InscripcionRequest() {}

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public List<Long> getCursoIds() { return cursoIds; }
    public void setCursoIds(List<Long> cursoIds) { this.cursoIds = cursoIds; }
}