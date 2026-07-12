package com.duoc.plataforma_educativa.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Mensaje que el BFF (bff-inscripciones) publica en la cola
 * "solicitud-inscripcion.queue" cuando un estudiante solicita inscribirse.
 * plataforma-educativa lo consume, persiste la inscripción y publica
 * a su vez el resumen en "inscripciones.queue" (flujo ya existente).
 */
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