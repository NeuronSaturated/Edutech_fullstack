package com.edutech.Edutech.repository;

import com.edutech.Edutech.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para la entidad Notificacion que extiende JpaRepository
// Proporciona métodos CRUD estándar para la entidad Notificacion
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {}
