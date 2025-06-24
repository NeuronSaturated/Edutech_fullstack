package com.edutech.Edutech.service;

import java.time.LocalDate;
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

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.model.Estudiante;
import com.edutech.Edutech.model.Inscripcion;
import com.edutech.Edutech.repository.CursoRepository;
import com.edutech.Edutech.repository.EstudianteRepository;
import com.edutech.Edutech.repository.InscripcionRepository;

/**
 * Tests unitarios para InscripcionService.
 */
@ExtendWith(MockitoExtension.class)
public class InscripcionServiceTest {

    @Mock
    private InscripcionRepository repo;

    @Mock
    private EstudianteRepository estudianteRepo;

    @Mock
    private CursoRepository cursoRepo;

    @InjectMocks
    private InscripcionService service;

    @Test
    void testListarTodos() {
        // 1. Preparamos un par de inscripciones
        Inscripcion i1 = new Inscripcion(); i1.setId(1L);
        Inscripcion i2 = new Inscripcion(); i2.setId(2L);

        // 2. Mock del repo
        when(repo.findAll()).thenReturn(List.of(i1, i2));

        // 3. Llamada al servicio
        List<Inscripcion> resultado = service.listarTodos();

        // 4. Asserts
        assertNotNull(resultado, "La lista no debe ser null");
        assertEquals(2, resultado.size(), "Deben venir 2 inscripciones");
        assertEquals(1L, resultado.get(0).getId(), "El primer ID debe ser 1");

        // 5. Verificación de interacciones
        verify(repo, times(1)).findAll();
    }

    @Test
    void testBuscarPorIdEncontrado() {
        // 1. Preparamos
        Inscripcion i = new Inscripcion(); i.setId(5L);

        // 2. Mock
        when(repo.findById(5L)).thenReturn(Optional.of(i));

        // 3. Llamada
        Optional<Inscripcion> opt = service.buscarPorId(5L);

        // 4. Asserts
        assertTrue(opt.isPresent(), "Debe encontrar la inscripcion");
        assertEquals(5L, opt.get().getId(), "ID debe coincidir");

        // 5. Verificar repo
        verify(repo, times(1)).findById(5L);
    }

    @Test
    void testBuscarPorIdNoEncontrado() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        Optional<Inscripcion> opt = service.buscarPorId(99L);

        assertFalse(opt.isPresent(), "No debe encontrar inscripcion");
        verify(repo).findById(99L);
    }

    @Test
    void testInscribirEstudianteExitoso() {
        // 1. Preparamos estudiante y curso
        Estudiante est = new Estudiante(); est.setId(10L);
        Curso cur = new Curso(); cur.setId(20L);
        Inscripcion saved = new Inscripcion();
        saved.setId(7L);
        saved.setEstudiante(est);
        saved.setCurso(cur);
        saved.setFechaInscripcion(LocalDate.now());

        // 2. Mocks: no existe inscripción previa, repos retornan entidad
        when(repo.findByEstudianteIdAndCursoId(10L, 20L)).thenReturn(Optional.empty());
        when(estudianteRepo.findById(10L)).thenReturn(Optional.of(est));
        when(cursoRepo.findById(20L)).thenReturn(Optional.of(cur));
        when(repo.save(any())).thenReturn(saved);

        // 3. Llamada
        Optional<Inscripcion> opt = service.inscribirEstudiante(10L, 20L);

        // 4. Asserts
        assertTrue(opt.isPresent(), "Debe inscribir correctamente");
        assertEquals(7L, opt.get().getId(), "ID asignado debe ser 7");

        // 5. Verificaciones
        verify(repo).findByEstudianteIdAndCursoId(10L, 20L);
        verify(repo).save(any());
    }

    @Test
    void testInscribirEstudianteDuplicado() {
        Inscripcion existing = new Inscripcion(); existing.setId(3L);
        when(repo.findByEstudianteIdAndCursoId(1L, 2L)).thenReturn(Optional.of(existing));

        Optional<Inscripcion> opt = service.inscribirEstudiante(1L, 2L);

        assertTrue(opt.isEmpty(), "No debe inscribir duplicado");
        verify(repo).findByEstudianteIdAndCursoId(1L, 2L);
        verify(repo, never()).save(any());
    }

    @Test
    void testInscribirEstudianteDatosFaltantes() {
        // 1. Mocks: no dup, pero estudiante no existe
        when(repo.findByEstudianteIdAndCursoId(5L, 6L)).thenReturn(Optional.empty());
        when(estudianteRepo.findById(5L)).thenReturn(Optional.empty());

        Optional<Inscripcion> opt1 = service.inscribirEstudiante(5L, 6L);
        assertTrue(opt1.isEmpty(), "Falla por estudiante faltante");

        // 2. Mocks: estudiante existe, curso no
        Estudiante est = new Estudiante(); est.setId(5L);
        when(estudianteRepo.findById(5L)).thenReturn(Optional.of(est));
        when(cursoRepo.findById(6L)).thenReturn(Optional.empty());

        Optional<Inscripcion> opt2 = service.inscribirEstudiante(5L, 6L);
        assertTrue(opt2.isEmpty(), "Falla por curso faltante");

        verify(repo, times(2)).findByEstudianteIdAndCursoId(5L, 6L);
        verify(repo, never()).save(any());
    }

    @Test
    void testActualizarExistente() {
        Inscripcion old = new Inscripcion(); old.setId(4L);
        Inscripcion upd = new Inscripcion();
        Curso newCur = new Curso(); newCur.setId(30L);
        Estudiante newEst = new Estudiante(); newEst.setId(40L);
        upd.setCurso(newCur);
        upd.setEstudiante(newEst);
        upd.setFechaInscripcion(LocalDate.of(2025,1,1));

        when(repo.findById(4L)).thenReturn(Optional.of(old));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<Inscripcion> res = service.actualizar(4L, upd);

        assertTrue(res.isPresent(), "Debe actualizar");
        assertEquals(30L, res.get().getCurso().getId());
        assertEquals(40L, res.get().getEstudiante().getId());
        assertEquals(LocalDate.of(2025,1,1), res.get().getFechaInscripcion());
    }

    @Test
    void testActualizarNoExistente() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        Optional<Inscripcion> res = service.actualizar(99L, new Inscripcion());
        assertTrue(res.isEmpty(), "No debe actualizar no existente");
    }

    @Test
    void testEliminar() {
        when(repo.existsById(5L)).thenReturn(true);
        boolean ok = service.eliminar(5L);
        assertTrue(ok, "Debe eliminar si existe");
        verify(repo).deleteById(5L);
    }

    @Test
    void testEliminarNoExiste() {
        when(repo.existsById(9L)).thenReturn(false);
        assertFalse(service.eliminar(9L), "No elimina si no existe");
        verify(repo, never()).deleteById(any());
    }

    @Test
    void testObtenerInscripcionesPorEstudiante() {
        Inscripcion i = new Inscripcion(); i.setId(8L);
        when(repo.findByEstudianteId(2L)).thenReturn(List.of(i));

        List<Inscripcion> lista = service.obtenerInscripcionesPorEstudiante(2L);
        assertEquals(1, lista.size());
        assertEquals(8L, lista.get(0).getId());
    }

    @Test
    void testListarCursosPorEstudiante() {
        // 1. Preparamos ids y entidades
        when(repo.findCursoIdsByEstudianteId(3L)).thenReturn(List.of(100L, 200L));
        Curso c1 = new Curso(); c1.setId(100L);
        Curso c2 = new Curso(); c2.setId(200L);
        when(cursoRepo.findByIdIn(List.of(100L,200L))).thenReturn(List.of(c1,c2));

        // 2. Llamada
        List<Curso> cursos = service.listarCursosPorEstudiante(3L);

        // 3. Asserts
        assertEquals(2, cursos.size());
        assertEquals(100L, cursos.get(0).getId());
    }

    @Test
    void testObtenerEstudiantesPorCursoId() {
        Estudiante e = new Estudiante(); e.setId(50L);
        when(repo.findEstudiantesByCursoId(7L)).thenReturn(List.of(e));

        List<Estudiante> estudios = service.obtenerEstudiantesPorCursoId(7L);
        assertEquals(1, estudios.size());
        assertEquals(50L, estudios.get(0).getId());
    }
}
