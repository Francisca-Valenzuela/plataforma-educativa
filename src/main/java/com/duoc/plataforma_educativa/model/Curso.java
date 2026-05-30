package com.duoc.plataforma_educativa.model;


import jakarta.persistence.*;

@Entity
@Table(name = "CURSOS")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String instructor;

    @Column(nullable = false)
    private Integer duracion; // en horas

    @Column(nullable = false)
    private Double costo;

    // Constructores
    public Curso() {}

    public Curso(String nombre, String instructor, Integer duracion, Double costo) {
        this.nombre = nombre;
        this.instructor = instructor;
        this.duracion = duracion;
        this.costo = costo;
    }

    // Getters y Setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public String getInstructor() { 
        return instructor; 
    }

    public void setInstructor(String instructor) { 
        this.instructor = instructor; 
    }

    public Integer getDuracion() { 
        return duracion; 
    }

    public void setDuracion(Integer duracion) { 
        this.duracion = duracion; 
    }

    public Double getCosto() { 
        return costo; 
    }

    public void setCosto(Double costo) { 
        this.costo = costo; 
    }
}