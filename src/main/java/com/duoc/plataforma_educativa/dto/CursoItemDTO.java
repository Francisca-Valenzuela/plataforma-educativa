package com.duoc.plataforma_educativa.dto;

import java.io.Serializable;

/**
 * Representa el detalle de un curso dentro del mensaje de resumen de
 * inscripción que se envía a la cola RabbitMQ.
 */
public class CursoItemDTO implements Serializable {

    private String nombre;
    private String instructor;
    private Integer duracion;
    private Double costo;

    public CursoItemDTO() {}

    public CursoItemDTO(String nombre, String instructor, Integer duracion, Double costo) {
        this.nombre = nombre;
        this.instructor = instructor;
        this.duracion = duracion;
        this.costo = costo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }
}
