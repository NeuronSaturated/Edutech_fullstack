package com.edutech.Edutech.model;

import jakarta.persistence.*;

// Indica que esta clase es una entidad JPA y se mapeará a una tabla en la base de datos
@Entity
public class Estudiante {

    // Clave primaria con generación automática (auto-incremental)
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del estudiante
    private String nombre;

    // Correo electrónico, con restricción de ser único en la base de datos
    @Column(unique = true)
    private String correo;

    // Carrera o área de estudio del estudiante
    private String carrera;

    // Contraseña para acceso (recomendable en la práctica almacenar hashed)
    private String password;

    // Dirección del estudiante
    private String direccion;

    // Campo para el rol del usuario, puede ser "ESTUDIANTE", "DOCENTE" o "ADMIN"
    private String rol;

    // Getters y setters para acceder y modificar cada atributo

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
