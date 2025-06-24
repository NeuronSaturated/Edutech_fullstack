package com.edutech.Edutech.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edutech.Edutech.model.Notificacion;
import com.edutech.Edutech.repository.NotificacionRepository;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repo;

    @InjectMocks
    private NotificacionService service;

    @Test
    void testListarTodasNotificaciones() {
        // 1. Preparamos dos notificaciones
        Notificacion n1 = new Notificacion();
        n1.setId(1L);
        n1.setMensaje("Mensaje 1");

        Notificacion n2 = new Notificacion();
        n2.setId(2L);
        n2.setMensaje("Mensaje 2");

        // 2. Mock del repo
        when(repo.findAll()).thenReturn(List.of(n1, n2));

        // 3. Llamada al servicio
        List<Notificacion> resultado = service.listarTodos();

        // 4. Asserts básicos
        assertNotNull(resultado, "La lista no debe ser null");
        assertEquals(2, resultado.size(), "Deben venir 2 notificaciones");
        assertEquals("Mensaje 1", resultado.get(0).getMensaje(), "El primer mensaje debe coincidir");

        // 5. Verificar invocación
        verify(repo, times(1)).findAll();
    }

    @Test
    void testBuscarPorIdEncontrado() {
        // 1. Preparamos una notificación
        Notificacion n = new Notificacion();
        n.setId(5L);
        n.setMensaje("Hola");

        // 2. Mock del repo
        when(repo.findById(5L)).thenReturn(Optional.of(n));

        // 3. Llamada al servicio
        Optional<Notificacion> opt = service.buscarPorId(5L);

        // 4. Asserts
        assertTrue(opt.isPresent(), "Debe encontrar la notificación");
        assertEquals("Hola", opt.get().getMensaje(), "El mensaje debe coincidir");

        // 5. Verificar invocación
        verify(repo, times(1)).findById(5L);
    }

    @Test
    void testBuscarPorIdNoEncontrado() {
        // Mock que no existe
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // Llamada al servicio
        Optional<Notificacion> opt = service.buscarPorId(99L);

        // Assert
        assertFalse(opt.isPresent(), "No debe encontrar notificación");
        verify(repo, times(1)).findById(99L);
    }

    @Test
    void testGuardarNotificacion() {
        // 1. Preparamos nueva notificación (sin ID)
        Notificacion toSave = new Notificacion();
        toSave.setMensaje("Nuevo mensaje");

        // 2. Mock al guardar, devuelve con ID
        Notificacion saved = new Notificacion();
        saved.setId(7L);
        saved.setMensaje("Nuevo mensaje");
        when(repo.save(toSave)).thenReturn(saved);

        // 3. Llamada al servicio
        Notificacion result = service.guardar(toSave);

        // 4. Asserts
        assertNotNull(result.getId(), "La notificación debe tener ID");
        assertEquals(7L, result.getId(), "ID asignado debe ser 7");
        assertEquals("Nuevo mensaje", result.getMensaje(), "El mensaje debe conservarse");

        // 5. Verificar invocación
        verify(repo, times(1)).save(toSave);
    }

    @Test
    void testActualizarExistente() {
        // 1. Preparamos notificación existente y cambios
        Notificacion existing = new Notificacion();
        existing.setId(3L);
        existing.setMensaje("Antiguo");

        Notificacion cambios = new Notificacion();
        cambios.setMensaje("Actualizado");

        // 2. Mock de findById y save
        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        // 3. Llamada al servicio
        Optional<Notificacion> opt = service.actualizar(3L, cambios);

        // 4. Asserts
        assertTrue(opt.isPresent(), "Debe encontrar y actualizar");
        assertEquals("Actualizado", opt.get().getMensaje(), "El mensaje debe actualizarse");

        // 5. Verificar invocaciones
        verify(repo, times(1)).findById(3L);
        verify(repo, times(1)).save(existing);
    }

    @Test
    void testActualizarNoExistente() {
        // Mock que no existe
        when(repo.findById(42L)).thenReturn(Optional.empty());

        // Llamada al servicio
        Optional<Notificacion> opt = service.actualizar(42L, new Notificacion());

        // Assert
        assertFalse(opt.isPresent(), "No debe actualizar si no existe");
        verify(repo, times(1)).findById(42L);
        verify(repo, never()).save(any());
    }

    @Test
    void testEliminarExistente() {
        // Mock que existe
        when(repo.existsById(9L)).thenReturn(true);

        // Llamada al servicio
        boolean borrado = service.eliminar(9L);

        // Asserts
        assertTrue(borrado, "Debe eliminar si existe");
        verify(repo, times(1)).existsById(9L);
        verify(repo, times(1)).deleteById(9L);
    }

    @Test
    void testEliminarNoExiste() {
        // Mock que no existe
        when(repo.existsById(10L)).thenReturn(false);

        // Llamada al servicio
        boolean borrado = service.eliminar(10L);

        // Assert
        assertFalse(borrado, "No elimina si no existe");
        verify(repo, times(1)).existsById(10L);
        verify(repo, never()).deleteById(any());
    }
}
