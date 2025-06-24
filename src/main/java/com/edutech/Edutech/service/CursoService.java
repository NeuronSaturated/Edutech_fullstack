package com.edutech.Edutech.service;

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Clase de servicio que contiene la lógica de negocio para Cursos
@Service
public class CursoService {

    // Inyección del repositorio para acceder a la base de datos
    @Autowired
    private CursoRepository repo;

    // Devuelve la lista de todos los cursos registrados
    public List<Curso> listarTodos() {
        return repo.findAll();
    }

    // Busca un curso por su id, devolviendo Optional para manejar ausencia
    public Optional<Curso> buscarPorId(Long id) {
        return repo.findById(id);
    }

    // Lista cursos filtrados por el ID del docente asignado
    public List<Curso> listarPorDocente(Long docenteId) {
        return repo.findByDocenteId(docenteId);
    }

    // Guarda un nuevo curso o actualiza uno existente
    public Curso guardar(Curso curso) {
        return repo.save(curso);
    }

    // Actualiza un curso existente, si lo encuentra por id
    // Modifica nombre, descripción y docente; devuelve el curso actualizado envuelto en Optional
    public Optional<Curso> actualizar(Long id, Curso curso) {
        return repo.findById(id).map(c -> {
            c.setNombre(curso.getNombre());
            c.setDescripcion(curso.getDescripcion());
            c.setDocente(curso.getDocente());
            return repo.save(c);
        });
    }

    // Elimina un curso si existe por id, devuelve true si se eliminó o false si no existía
    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
