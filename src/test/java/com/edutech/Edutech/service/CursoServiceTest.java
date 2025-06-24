package com.edutech.Edutech.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.repository.CursoRepository;

@ExtendWith(MockitoExtension.class)
public class CursoServiceTest {

    @Mock
    private CursoRepository repo;

    @InjectMocks
    private CursoService service;  // método real: listarTodos() :contentReference[oaicite:0]{index=0}

    @Test
    void testListarTodosCursos() {
        // 1. Preparamos dos Cursos usando constructor vacío + setters
        Curso c1 = new Curso();
        c1.setId(1L);
        c1.setNombre("Curso 1");
        c1.setDescripcion("Desc 1");

        Curso c2 = new Curso();
        c2.setId(2L);
        c2.setNombre("Curso 2");
        c2.setDescripcion("Desc 2");

        // 2. Configuramos el mock para devolver nuestra lista simulada
        when(repo.findAll()).thenReturn(List.of(c1, c2));

        // 3. Llamamos al método que realmente existe en tu servicio
        List<Curso> resultado = service.listarTodos();  

        // 4. Verificaciones
        assertNotNull(resultado, "La lista no debe ser null");
        assertEquals(2, resultado.size(), "Deben venir 2 cursos");
        assertEquals(1L, resultado.get(0).getId(), "El primer ID debe ser 1");
        assertEquals("Curso 1", resultado.get(0).getNombre(), "El primer nombre debe coincidir");

        // 5. Aseguramos que repo.findAll() se invocó exactamente una vez
        verify(repo, times(1)).findAll();
    }
}
