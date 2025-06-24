package com.edutech.Edutech.service;

import com.edutech.Edutech.model.Notificacion;
import com.edutech.Edutech.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Servicio que maneja la lógica de negocio para Notificaciones
@Service
public class NotificacionService {

    // Inyección del repositorio para acceder a los datos de Notificacion
    @Autowired
    private NotificacionRepository repo;

    // Devuelve la lista completa de notificaciones almacenadas
    public List<Notificacion> listarTodos() {
        return repo.findAll();
    }

    // Busca una notificación por su id, devuelve Optional para manejar ausencia
    public Optional<Notificacion> buscarPorId(Long id) {
        return repo.findById(id);
    }

    // Guarda una nueva notificación o actualiza una existente
    public Notificacion guardar(Notificacion notificacion) {
        return repo.save(notificacion);
    }

    // Actualiza una notificación existente si se encuentra por id
    public Optional<Notificacion> actualizar(Long id, Notificacion notificacion) {
        return repo.findById(id).map(n -> {
            n.setDestino(notificacion.getDestino());
            n.setMensaje(notificacion.getMensaje());
            return repo.save(n);
        });
    }

    // Elimina una notificación si existe, devuelve true si se eliminó correctamente
    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
