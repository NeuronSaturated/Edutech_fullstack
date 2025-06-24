package com.edutech.Edutech.service;

import com.edutech.Edutech.model.Estudiante;
import com.edutech.Edutech.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Servicio que maneja la lógica de negocio para la entidad Estudiante
@Service
public class EstudianteService {

    // Inyección del repositorio para acceder a datos de estudiantes
    @Autowired
    private EstudianteRepository repo;

    // Codificador de contraseñas para proteger la información sensible
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Devuelve todos los estudiantes registrados
    public List<Estudiante> listarTodos() {
        return repo.findAll();
    }

    // Busca un estudiante por su id
    public Optional<Estudiante> buscarPorId(Long id) {
        return repo.findById(id);
    }

    // Busca un estudiante por su correo electrónico
    public Optional<Estudiante> buscarPorCorreo(String correo) {
        return repo.findByCorreo(correo);
    }

    // Guarda un estudiante nuevo validando correo único y encriptando contraseña
    public Estudiante guardar(Estudiante estudiante) {
        // Verifica si el correo ya está registrado para evitar duplicados
        if (repo.existsByCorreo(estudiante.getCorreo())) {
            throw new RuntimeException("Correo ya registrado");
        }

        // Encripta la contraseña antes de guardar para seguridad
        String encodedPassword = passwordEncoder.encode(estudiante.getPassword());
        estudiante.setPassword(encodedPassword);

        // Si no se envía un rol, asigna "ESTUDIANTE" por defecto
        if (estudiante.getRol() == null || estudiante.getRol().isBlank()) {
            estudiante.setRol("ESTUDIANTE");
        }

        // Guarda el estudiante en la base de datos
        return repo.save(estudiante);
    }

    // Actualiza un estudiante existente sin modificar password ni rol
    public Optional<Estudiante> actualizar(Long id, Estudiante estudiante) {
        return repo.findById(id).map(est -> {
            est.setNombre(estudiante.getNombre());
            est.setCorreo(estudiante.getCorreo());
            est.setCarrera(estudiante.getCarrera());
            est.setDireccion(estudiante.getDireccion());
            // No actualiza password ni rol aquí por seguridad y control
            return repo.save(est);
        });
    }

    // Elimina un estudiante si existe, devuelve true si se eliminó
    public boolean eliminar(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    // Lógica de login: verifica correo y contraseña
    public Optional<Estudiante> login(String correo, String password) {
        Optional<Estudiante> optEst = repo.findByCorreo(correo);
        if (optEst.isPresent()) {
            Estudiante est = optEst.get();
            // Verifica que la contraseña recibida coincida con la encriptada
            if (passwordEncoder.matches(password, est.getPassword())) {
                return Optional.of(est);
            }
        }
        return Optional.empty();  // Retorna vacío si no coincide o no existe
    }
}
