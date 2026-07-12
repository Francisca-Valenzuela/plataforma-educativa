package com.duoc.bff_inscripciones.dto;

import java.io.Serializable;
import java.util.List;

public class SolicitudInscripcionMensaje implements Serializable {

    private String nombreEstudiante;
    private List<Long> cursoIds;

    public SolicitudInscripcionMensaje() {}

    public SolicitudInscripcionMensaje(String nombreEstudiante, List<Long> cursoIds) {
        this.nombreEstudiante = nombreEstudiante;
        this.cursoIds = cursoIds;
    }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public List<Long> getCursoIds() { return cursoIds; }
    public void setCursoIds(List<Long> cursoIds) { this.cursoIds = cursoIds; }
}