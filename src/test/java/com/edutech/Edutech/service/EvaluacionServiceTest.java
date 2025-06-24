package com.edutech.Edutech.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.edutech.Edutech.model.Evaluacion;
import com.edutech.Edutech.repository.EvaluacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EvaluacionServiceTest {

    @Mock
    private EvaluacionRepository repo;

    @InjectMocks
    private EvaluacionService service;

    @Test
    void testListarTodosEvaluaciones() {
        // 1. Preparamos dos Evaluaciones
        Evaluacion ev1 = new Evaluacion();
        ev1.setId(1L);
        ev1.setTitulo("Parcial 1");
        ev1.setNota(4.5);

        Evaluacion ev2 = new Evaluacion();
        ev2.setId(2L);
        ev2.setTitulo("Parcial 2");
        ev2.setNota(5.0);

        // 2. Configuramos el mock
        when(repo.findAll()).thenReturn(List.of(ev1, ev2));

        // 3. Llamamos al servicio
        List<Evaluacion> resultado = service.listarTodos();

        // 4. Verificaciones
        assertNotNull(resultado, "La lista no debe ser null");
        assertEquals(2, resultado.size(), "Deben venir 2 evaluaciones");
        assertEquals("Parcial 1", resultado.get(0).getTitulo(), "El primer título debe coincidir");

        // 5. Verificamos la interacción
        verify(repo, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_EvaluacionEncontrada() {
        // 1. Preparamos una Evaluacion
        Evaluacion ev = new Evaluacion();
        ev.setId(10L);
        ev.setTitulo("Final");
        ev.setNota(6.0);

        // 2. Configuramos el mock
        when(repo.findById(10L)).thenReturn(Optional.of(ev));

        // 3. Llamamos al servicio
        Optional<Evaluacion> opt = service.buscarPorId(10L);

        // 4. Verificaciones
        assertTrue(opt.isPresent(), "Debería encontrar la evaluación");
        assertEquals("Final", opt.get().getTitulo(), "El título debe coincidir");

        verify(repo, times(1)).findById(10L);
    }

    @Test
    void testBuscarPorId_EvaluacionNoEncontrada() {
        // 1. Configuramos el mock vacío
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // 2. Llamamos al servicio
        Optional<Evaluacion> opt = service.buscarPorId(99L);

        // 3. Verificaciones
        assertTrue(opt.isEmpty(), "No debe encontrar la evaluación");
        verify(repo, times(1)).findById(99L);
    }

    @Test
    void testGuardarEvaluacion() {
        // 1. Preparamos la Evaluacion sin ID
        Evaluacion ev = new Evaluacion();
        ev.setTitulo("Quiz");
        ev.setNota(3.5);

        Evaluacion saved = new Evaluacion();
        saved.setId(100L);
        saved.setTitulo("Quiz");
        saved.setNota(3.5);

        // 2. Configuramos el mock
        when(repo.save(ev)).thenReturn(saved);

        // 3. Llamamos al servicio
        Evaluacion resultado = service.guardar(ev);

        // 4. Verificaciones
        assertNotNull(resultado.getId(), "El ID no debe ser null");
        assertEquals(100L, resultado.getId(), "El ID generado debe ser 100");

        verify(repo, times(1)).save(ev);
    }

    @Test
    void testActualizar_EvaluacionExistente() {
        // 1. Preparamos la evaluación original y la actualización
        Evaluacion original = new Evaluacion();
        original.setId(50L);
        original.setTitulo("Ordinario");
        original.setNota(2.0);

        Evaluacion updates = new Evaluacion();
        updates.setTitulo("Ordinario Modificado");
        updates.setNota(2.5);

        Evaluacion merged = new Evaluacion();
        merged.setId(50L);
        merged.setTitulo("Ordinario Modificado");
        merged.setNota(2.5);

        // 2. Configuramos el mock
        when(repo.findById(50L)).thenReturn(Optional.of(original));
        when(repo.save(any(Evaluacion.class))).thenReturn(merged);

        // 3. Llamamos al servicio
        Optional<Evaluacion> opt = service.actualizar(50L, updates);

        // 4. Verificaciones
        assertTrue(opt.isPresent(), "Debería actualizar la evaluación");
        assertEquals("Ordinario Modificado", opt.get().getTitulo());
        assertEquals(2.5, opt.get().getNota());

        verify(repo, times(1)).findById(50L);
        verify(repo, times(1)).save(any(Evaluacion.class));
    }

    @Test
    void testActualizar_EvaluacionNoExiste() {
        // 1. Configuramos mock para no encontrar
        when(repo.findById(123L)).thenReturn(Optional.empty());

        // 2. Llamamos al servicio
        Optional<Evaluacion> opt = service.actualizar(123L, new Evaluacion());

        // 3. Verificaciones
        assertTrue(opt.isEmpty(), "No debe actualizar nada");
        verify(repo, times(1)).findById(123L);
        verify(repo, never()).save(any());
    }

    @Test
    void testEliminar_EvaluacionExistente() {
        // 1. Configuramos mock para indicar existencia
        when(repo.existsById(77L)).thenReturn(true);

        // 2. Llamamos al servicio
        boolean borrado = service.eliminar(77L);

        // 3. Verificaciones
        assertTrue(borrado, "Debe devolver true si elimina");
        verify(repo, times(1)).existsById(77L);
        verify(repo, times(1)).deleteById(77L);
    }

    @Test
    void testEliminar_EvaluacionNoExiste() {
        // 1. Configuramos mock para indicar no existencia
        when(repo.existsById(999L)).thenReturn(false);

        // 2. Llamamos al servicio
        boolean borrado = service.eliminar(999L);

        // 3. Verificaciones
        assertFalse(borrado, "Debe devolver false si no existe");
        verify(repo, times(1)).existsById(999L);
        verify(repo, never()).deleteById(any());
    }
}
