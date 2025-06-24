package com.edutech.Edutech.repository;

import com.edutech.Edutech.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repositorio para la entidad Estudiante que hereda métodos CRUD básicos de JpaRepository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Método para buscar un estudiante por su correo electrónico
    // Retorna un Optional que puede contener o no un Estudiante
    Optional<Estudiante> findByCorreo(String correo);

    // Método para verificar si ya existe un estudiante con el correo dado
    boolean existsByCorreo(String correo);
}
