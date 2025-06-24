package com.edutech.Edutech.repository;

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.model.Estudiante;
import com.edutech.Edutech.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Buscar por curso y estudiante para validar duplicados
    Optional<Inscripcion> findByEstudianteIdAndCursoId(Long estudianteId, Long cursoId);

    // Lista de inscripciones por estudiante
    List<Inscripcion> findByEstudianteId(Long estudianteId);

    // Lista de inscripciones por curso
    List<Inscripcion> findByCursoId(Long cursoId);

    // Obtener solo IDs de cursos de un estudiante
    @Query("SELECT i.curso.id FROM Inscripcion i WHERE i.estudiante.id = :idEstudiante")
    List<Long> findCursoIdsByEstudianteId(Long idEstudiante);

    // Obtener lista de estudiantes inscritos en un curso
    @Query("SELECT i.estudiante FROM Inscripcion i WHERE i.curso.id = :idCurso")
    List<Estudiante> findEstudiantesByCursoId(Long idCurso);
}
