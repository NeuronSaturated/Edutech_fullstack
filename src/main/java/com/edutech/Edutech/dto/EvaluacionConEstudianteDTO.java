package com.edutech.Edutech.dto;

import java.time.LocalDate;

public class EvaluacionConEstudianteDTO {
    private Long id;
    private Double nota;
    private Double ponderacion;
    private LocalDate fecha;
    private String titulo;

    private Long estudianteId;
    private String estudianteNombre;

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Double getNota() {
        return nota;
    }
    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Double getPonderacion() {
        return ponderacion;
    }
    public void setPonderacion(Double ponderacion) {
        this.ponderacion = ponderacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }
    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }
    public void setEstudianteNombre(String estudianteNombre) {
        this.estudianteNombre = estudianteNombre;
    }
}
