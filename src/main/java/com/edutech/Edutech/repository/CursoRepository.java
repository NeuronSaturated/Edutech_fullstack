package com.edutech.Edutech.repository;

import com.edutech.Edutech.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Interfaz que extiende JpaRepository para manejar la entidad Curso
public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Obtener cursos por ID del docente
    List<Curso> findByDocenteId(Long docenteId);

    // Obtener cursos por lista de IDs (para cursos inscritos)
    List<Curso> findByIdIn(List<Long> ids);
}

