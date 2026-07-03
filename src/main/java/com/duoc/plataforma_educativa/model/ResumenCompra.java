package com.duoc.plataforma_educativa.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Nueva tabla (Semana 7) donde el CONSUMIDOR de la cola RabbitMQ persiste el
 * resumen de compra recibido de forma asíncrona, en la base de datos Oracle Cloud.
 *
 * Es independiente de la tabla INSCRIPCIONES (creada de forma síncrona en la
 * Semana 1): esta tabla representa el resultado del procesamiento asíncrono
 * del mensaje leído desde la cola "inscripciones.queue".
 */
@Entity
@Table(name = "RESUMEN_COMPRA")
public class ResumenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INSCRIPCION_ID", nullable = false)
    private Long inscripcionId;

    @Column(name = "NOMBRE_ESTUDIANTE", nullable = false)
    private String nombreEstudiante;

    @Column(name = "DETALLE_CURSOS", length = 2000)
    private String detalleCursos;

    @Column(name = "TOTAL_PAGAR", nullable = false)
    private Double totalPagar;

    @Column(name = "FECHA_INSCRIPCION", nullable = false)
    private String fechaInscripcion;

    @Column(name = "FECHA_PROCESADO", nullable = false)
    private LocalDateTime fechaProcesado;

    public ResumenCompra() {}

    public ResumenCompra(Long inscripcionId, String nombreEstudiante, String detalleCursos,
                          Double totalPagar, String fechaInscripcion) {
        this.inscripcionId = inscripcionId;
        this.nombreEstudiante = nombreEstudiante;
        this.detalleCursos = detalleCursos;
        this.totalPagar = totalPagar;
        this.fechaInscripcion = fechaInscripcion;
        this.fechaProcesado = LocalDateTime.now();
    }

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public Long getInscripcionId() { 
        return inscripcionId; 
    }

    public void setInscripcionId(Long inscripcionId) { 
        this.inscripcionId = inscripcionId; 
    }

    public String getNombreEstudiante() { 
        return nombreEstudiante; 
    }

    public void setNombreEstudiante(String nombreEstudiante) { 
        this.nombreEstudiante = nombreEstudiante; 
    }

    public String getDetalleCursos() { 
        return detalleCursos; 
    }
    public void setDetalleCursos(String detalleCursos) { 
        this.detalleCursos = detalleCursos; 
    }

    public Double getTotalPagar() { 
        return totalPagar; 
    }

    public void setTotalPagar(Double totalPagar) { 
        this.totalPagar = totalPagar; 
    }

    public String getFechaInscripcion() { 
        return fechaInscripcion; 
    }

    public void setFechaInscripcion(String fechaInscripcion) { 
        this.fechaInscripcion = fechaInscripcion; 
    }

    public LocalDateTime getFechaProcesado() { 
        return fechaProcesado; 
    }

    public void setFechaProcesado(LocalDateTime fechaProcesado) { 
        this.fechaProcesado = fechaProcesado; }
}