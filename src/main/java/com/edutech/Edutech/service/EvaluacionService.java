package com.edutech.Edutech.service;

import com.edutech.Edutech.model.Evaluacion;
import com.edutech.Edutech.repository.CursoRepository;
import com.edutech.Edutech.repository.EstudianteRepository;
import com.edutech.Edutech.repository.EvaluacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.edutech.Edutech.dto.EvaluacionConEstudianteDTO;
import java.util.stream.Collectors;

// Servicio que maneja la lógica de negocio para Evaluaciones
@Service
public class EvaluacionService {

    // Inyección del repositorio para acceso a datos de Evaluacion
    @Autowired
    private EvaluacionRepository repo;

    // Devuelve una lista con todas las evaluaciones registradas
    public List<Evaluacion> listarTodos() {
        return repo.findAll();
    }

    // Busca una evaluación por su id, devuelve Optional para manejar ausencia
    public Optional<Evaluacion> buscarPorId(Long id) {
        return repo.findById(id);
    }

    // Guarda una nueva evaluación o actualiza una existente
    public Evaluacion guardar(Evaluacion evaluacion) {
        return repo.save(evaluacion);
    }

   public Optional<Evaluacion> actualizar(Long id, Evaluacion evaluacion) {
    return repo.findById(id).map(e -> {
        e.setCurso(evaluacion.getCurso()); // cambia esto
        e.setEstudiante(evaluacion.getEstudiante()); // también debes usar el objeto
        e.setNota(evaluacion.getNota());
        e.setFecha(evaluacion.getFecha());
        e.setPonderacion(evaluacion.getPonderacion());
        return repo.save(e);
    });
}



    // Elimina una evaluación si existe, devuelve true si se eliminó
    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public void guardarEvaluaciones(List<Evaluacion> evaluaciones) {
    for (Evaluacion e : evaluaciones) {
        repo.save(e);  // Guarda o actualiza cada evaluación
    }
}


public List<Evaluacion> obtenerPorCurso(Long idCurso) {
    return repo.findByCursoId(idCurso);
}



public List<EvaluacionConEstudianteDTO> obtenerEvaluacionesPorCurso(Long cursoId) {
    List<Evaluacion> evaluaciones = repo.findByCursoId(cursoId);
    
    return evaluaciones.stream().map(eval -> {
        EvaluacionConEstudianteDTO dto = new EvaluacionConEstudianteDTO();
        dto.setId(eval.getId());
        dto.setNota(eval.getNota());
        dto.setPonderacion(eval.getPonderacion());
        dto.setFecha(eval.getFecha());
        dto.setTitulo(eval.getTitulo());
        
        // 👇 Aquí accedemos al estudiante asociado
        if (eval.getEstudiante() != null) {
            dto.setEstudianteId(eval.getEstudiante().getId());
            dto.setEstudianteNombre(eval.getEstudiante().getNombre());
        } else {
            dto.setEstudianteNombre("Desconocido");
        }
        
        return dto;
    }).collect(Collectors.toList());
}



/*public void actualizarEvaluacionesEnLote(List<Evaluacion> evaluaciones) {
    for (Evaluacion eval : evaluaciones) {
        Optional<Evaluacion> existente = repo.findById(eval.getId());
        existente.ifPresent(e -> {
            e.setNota(eval.getNota());
            e.setPonderacion(eval.getPonderacion());
            e.setTitulo(eval.getTitulo()); // ✅ ACTUALIZA EL TÍTULO
            repo.save(e);
        });
    }
}*/
@Autowired
private EstudianteRepository estudianteRepo;

@Autowired
private CursoRepository cursoRepo;

/*public void actualizarEvaluacionesEnLote(List<Evaluacion> evaluaciones) {
    for (Evaluacion eval : evaluaciones) {
        Optional<Evaluacion> existente = repo.findById(eval.getId());
        existente.ifPresent(e -> {
            e.setNota(eval.getNota());
            e.setPonderacion(eval.getPonderacion());
            e.setTitulo(eval.getTitulo());

            // 👇 Reasignar estudiante y curso desde sus repositorios
            if (eval.getEstudiante() != null && eval.getEstudiante().getId() != null) {
                e.setEstudiante(estudianteRepo.findById(eval.getEstudiante().getId()).orElse(null));
            }
            if (eval.getCurso() != null && eval.getCurso().getId() != null) {
                e.setCurso(cursoRepo.findById(eval.getCurso().getId()).orElse(null));
            }

            repo.save(e);
        });
    }
}*/
/*public void actualizarEvaluacionesEnLote(List<Evaluacion> evaluaciones) {
    for (Evaluacion eval : evaluaciones) {
        Optional<Evaluacion> existente = repo.findById(eval.getId());
        existente.ifPresent(e -> {
            e.setNota(eval.getNota());
            e.setPonderacion(eval.getPonderacion());
            e.setTitulo(eval.getTitulo());

            // ✅ Solo asigna si están vacíos
            if (e.getEstudiante() == null && eval.getEstudiante() != null && eval.getEstudiante().getId() != null) {
                estudianteRepo.findById(eval.getEstudiante().getId()).ifPresent(e::setEstudiante);
            }
            if (e.getCurso() == null && eval.getCurso() != null && eval.getCurso().getId() != null) {
                cursoRepo.findById(eval.getCurso().getId()).ifPresent(e::setCurso);
            }

            repo.save(e);
        });
    }
}*/
public void actualizarEvaluacionesEnLote(List<Evaluacion> evaluaciones) {
    for (Evaluacion eval : evaluaciones) {
        Optional<Evaluacion> existente = repo.findById(eval.getId());
        existente.ifPresent(e -> {
            e.setNota(eval.getNota());
            e.setPonderacion(eval.getPonderacion());
            e.setTitulo(eval.getTitulo());

            // 🔁 Siempre reasignamos estudiante y curso (no solo si están en null)
            if (eval.getEstudiante() != null && eval.getEstudiante().getId() != null) {
                estudianteRepo.findById(eval.getEstudiante().getId()).ifPresent(e::setEstudiante);
            }
            if (eval.getCurso() != null && eval.getCurso().getId() != null) {
                cursoRepo.findById(eval.getCurso().getId()).ifPresent(e::setCurso);
            }

            repo.save(e);
        });
    }
}




}

