package com.edutech.Edutech.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.service.CursoService;
import com.edutech.Edutech.service.EstudianteService;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


@WebMvcTest(CursoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CursoControllerHateoasTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CursoService cursoService;

    @MockBean
    private EstudianteService estudianteService;

    @Test
    @DisplayName("GET /api/cursos/hateoas → colección HATEOAS")
    void listaCursosHateoas() throws Exception {
        // dado un par de cursos de ejemplo
        Curso c1 = new Curso();
        c1.setId(1L);
        c1.setNombre("A");
        c1.setDescripcion("desc");

        Curso c2 = new Curso();
        c2.setId(2L);
        c2.setNombre("B");
        c2.setDescripcion("desc");

        given(cursoService.listarTodos()).willReturn(List.of(c1, c2));

        mvc.perform(get("/api/cursos/hateoas"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/hal+json"))
           .andExpect(jsonPath("_embedded.cursoList[0].id").value(1))
           .andExpect(jsonPath("_embedded.cursoList[1].id").value(2))
           .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/cursos/{id}/hateoas → recurso HATEOAS individual")
    void cursoHateoas_porId() throws Exception {
        Curso curso = new Curso();
        curso.setId(5L);
        curso.setNombre("X");
        curso.setDescripcion("descX");

        given(cursoService.buscarPorId(5L)).willReturn(Optional.of(curso));

        mvc.perform(get("/api/cursos/5/hateoas"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/hal+json"))
           .andExpect(jsonPath("id").value(5))
           .andExpect(jsonPath("_links.self.href").exists())
           .andExpect(jsonPath("_links.todos-los-cursos.href").exists());
    }
}
