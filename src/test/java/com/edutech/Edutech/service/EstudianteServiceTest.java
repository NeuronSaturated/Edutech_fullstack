package com.edutech.Edutech.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edutech.Edutech.model.Estudiante;
import com.edutech.Edutech.repository.EstudianteRepository;

@ExtendWith(MockitoExtension.class)
public class EstudianteServiceTest {

    @Mock
    private EstudianteRepository repo;

    @InjectMocks
    private EstudianteService service;

    @Test
    void testListarTodosEstudiantes() {
        // 1. Preparamos dos Estudiantes
        Estudiante e1 = new Estudiante();
        e1.setId(1L);
        e1.setNombre("Ana María");

        Estudiante e2 = new Estudiante();
        e2.setId(2L);
        e2.setNombre("Luis Pérez");

        // 2. Configuramos el mock
        when(repo.findAll()).thenReturn(List.of(e1, e2));

        // 3. Llamamos al servicio
        List<Estudiante> resultado = service.listarTodos();

        // 4. Verificaciones
        assertNotNull(resultado, "La lista no debe ser null");
        assertEquals(2, resultado.size(), "Deben venir 2 estudiantes");
        assertEquals("Ana María", resultado.get(0).getNombre(), "El primer nombre debe coincidir");

        // 5. Verificamos la interacción con el repo
        verify(repo, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_EstudianteEncontrado() {
        // 1. Preparamos un Estudiante
        Estudiante e = new Estudiante();
        e.setId(5L);
        e.setNombre("María López");

        // 2. Configuramos el mock
        when(repo.findById(5L)).thenReturn(Optional.of(e));

        // 3. Llamamos al servicio
        Optional<Estudiante> opt = service.buscarPorId(5L);

        // 4. Verificaciones
        assertTrue(opt.isPresent(), "Debería encontrar al estudiante");
        assertEquals("María López", opt.get().getNombre(), "El nombre debe coincidir");

        verify(repo, times(1)).findById(5L);
    }

    @Test
    void testBuscarPorId_EstudianteNoEncontrado() {
        // 1. Configuramos el mock para devolver vacío
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // 2. Llamamos al servicio
        Optional<Estudiante> opt = service.buscarPorId(99L);

        // 3. Verificaciones
        assertTrue(opt.isEmpty(), "No debe encontrar al estudiante");
        verify(repo, times(1)).findById(99L);
    }
}
