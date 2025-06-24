package com.edutech.Edutech.model;

import jakarta.persistence.*;

// Entidad que representa una notificación en la base de datos
@Entity
public class Notificacion {

    // Clave primaria auto-generada
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Destinatario de la notificación (puede ser un usuario o sistema)
    private String destino;

    // Mensaje o contenido de la notificación
    private String mensaje;

    // Getters y setters para manipular los atributos

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
