package com.edutech.Edutech.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.model.Estudiante;
import com.edutech.Edutech.model.Inscripcion;
import com.edutech.Edutech.repository.CursoRepository;
import com.edutech.Edutech.repository.EstudianteRepository;
import com.edutech.Edutech.repository.InscripcionRepository;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository repo;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    public List<Inscripcion> listarTodos() {
        return repo.findAll();
    }

    public Optional<Inscripcion> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Optional<Inscripcion> inscribirEstudiante(Long idEstudiante, Long idCurso) {
        Optional<Inscripcion> existente = repo.findByEstudianteIdAndCursoId(idEstudiante, idCurso);
        if (existente.isPresent()) return Optional.empty();

        Optional<Estudiante> estudianteOpt = estudianteRepository.findById(idEstudiante);
        Optional<Curso> cursoOpt = cursoRepository.findById(idCurso);

        if (estudianteOpt.isEmpty() || cursoOpt.isEmpty()) return Optional.empty();

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudianteOpt.get());
        inscripcion.setCurso(cursoOpt.get());
        inscripcion.setFechaInscripcion(LocalDate.now());

        return Optional.of(repo.save(inscripcion));
    }

    public Optional<Inscripcion> actualizar(Long id, Inscripcion inscripcion) {
        return repo.findById(id).map(i -> {
            i.setCurso(inscripcion.getCurso());
            i.setEstudiante(inscripcion.getEstudiante());
            i.setFechaInscripcion(inscripcion.getFechaInscripcion());
            return repo.save(i);
        });
    }

    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Inscripcion> obtenerInscripcionesPorEstudiante(Long idEstudiante) {
        return repo.findByEstudianteId(idEstudiante);
    }

    public List<Curso> listarCursosPorEstudiante(Long idEstudiante) {
        List<Long> cursoIds = repo.findCursoIdsByEstudianteId(idEstudiante);
        return cursoRepository.findByIdIn(cursoIds);
    }

    public List<Estudiante> obtenerEstudiantesPorCursoId(Long cursoId) {
        return repo.findEstudiantesByCursoId(cursoId);
    }
}
