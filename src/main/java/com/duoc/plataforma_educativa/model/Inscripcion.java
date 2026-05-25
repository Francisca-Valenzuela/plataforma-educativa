package com.duoc.plataforma_educativa.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "INSCRIPCIONES")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nombreEstudiante;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "INSCRIPCION_CURSOS",
        joinColumns        = @JoinColumn(name = "INSCRIPCION_ID"),
        inverseJoinColumns = @JoinColumn(name = "CURSO_ID")
    )
    private List<Curso> cursos;

    @Column(nullable = false)
    private Double totalPagar;

    @Column(nullable = false)
    private LocalDateTime fechaInscripcion;

    // ── Constructores ──────────────────────────────────────
    public Inscripcion() {}

    public Inscripcion(String nombreEstudiante,
                       List<Curso> cursos,
                       Double totalPagar) {
        this.nombreEstudiante = nombreEstudiante;
        this.cursos           = cursos;
        this.totalPagar       = totalPagar;
        this.fechaInscripcion = LocalDateTime.now();
    }

    // ── Getters y Setters ──────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public List<Curso> getCursos() { return cursos; }
    public void setCursos(List<Curso> cursos) { this.cursos = cursos; }

    public Double getTotalPagar() { return totalPagar; }
    public void setTotalPagar(Double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }
}