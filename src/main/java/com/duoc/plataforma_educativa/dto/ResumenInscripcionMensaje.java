package com.duoc.plataforma_educativa.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Mensaje que el productor (creación de la inscripción) publica en la cola
 * "inscripciones.queue" y que el consumidor lee para persistir el resumen
 * de compra en la base de datos Oracle Cloud.
 *
 * Se serializa/deserializa como JSON gracias a Jackson2JsonMessageConverter
 * configurado en RabbitMQConfig.
 */
public class ResumenInscripcionMensaje implements Serializable {

    private Long inscripcionId;
    private String nombreEstudiante;
    private List<CursoItemDTO> cursos;
    private Double totalPagar;
    private String fechaInscripcion; // formato ISO-8601, para evitar problemas de serialización de LocalDateTime

    public ResumenInscripcionMensaje() {}

    public ResumenInscripcionMensaje(Long inscripcionId, String nombreEstudiante,
                                      List<CursoItemDTO> cursos, Double totalPagar,
                                      String fechaInscripcion) {
        this.inscripcionId = inscripcionId;
        this.nombreEstudiante = nombreEstudiante;
        this.cursos = cursos;
        this.totalPagar = totalPagar;
        this.fechaInscripcion = fechaInscripcion;
    }

    public Long getInscripcionId() { return inscripcionId; }
    public void setInscripcionId(Long inscripcionId) { this.inscripcionId = inscripcionId; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public List<CursoItemDTO> getCursos() { return cursos; }
    public void setCursos(List<CursoItemDTO> cursos) { this.cursos = cursos; }

    public Double getTotalPagar() { return totalPagar; }
    public void setTotalPagar(Double totalPagar) { this.totalPagar = totalPagar; }

    public String getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(String fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    @Override
    public String toString() {
        return "ResumenInscripcionMensaje{" +
                "inscripcionId=" + inscripcionId +
                ", nombreEstudiante='" + nombreEstudiante + '\'' +
                ", totalPagar=" + totalPagar +
                ", fechaInscripcion='" + fechaInscripcion + '\'' +
                '}';
    }
}