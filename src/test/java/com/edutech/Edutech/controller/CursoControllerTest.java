package com.edutech.Edutech.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.service.CursoService;
import com.edutech.Edutech.service.EstudianteService;
import com.edutech.Edutech.service.EvaluacionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CursoController.class)
// Esto desactiva los filtros (incluido Spring Security) en MockMvc
@AutoConfigureMockMvc(addFilters = false)
public class CursoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CursoService cursoService;

    @MockBean
    private EstudianteService estudianteService;

    @MockBean
    private EvaluacionService evaluacionService;

    @Test
    void getAllCursos_OK() throws Exception {
        Curso c1 = new Curso();
        c1.setId(1L);
        c1.setNombre("Curso 1");
        c1.setDescripcion("Desc 1");

        Curso c2 = new Curso();
        c2.setId(2L);
        c2.setNombre("Curso 2");
        c2.setDescripcion("Desc 2");

        when(cursoService.listarTodos()).thenReturn(List.of(c1, c2));

        mvc.perform(get("/api/cursos"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].id").value(1))
           .andExpect(jsonPath("$[0].nombre").value("Curso 1"))
           .andExpect(jsonPath("$[1].descripcion").value("Desc 2"));

        verify(cursoService, times(1)).listarTodos();
    }
}
