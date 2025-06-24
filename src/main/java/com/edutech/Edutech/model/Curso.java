package com.edutech.Edutech.model;

import jakarta.persistence.*;

// Indica que esta clase representa una entidad en la base de datos (una tabla)
@Entity
public class Curso {

    // Define la columna 'id' como la clave primaria con generación automática de valor (auto-incremental)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo para el nombre del curso
    private String nombre;

    // Campo para la descripción del curso
    private String descripcion;

    // Relación muchos a uno con la entidad Estudiante, que en este contexto representa al docente del curso
    @ManyToOne
    @JoinColumn(name = "docente_id") // Indica que en la tabla Curso habrá una columna 'docente_id' que es clave foránea a Estudiante
    private Estudiante docente;

    // Getters y setters para acceder y modificar los campos

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Estudiante getDocente() { return docente; }
    public void setDocente(Estudiante docente) { this.docente = docente; }
}
