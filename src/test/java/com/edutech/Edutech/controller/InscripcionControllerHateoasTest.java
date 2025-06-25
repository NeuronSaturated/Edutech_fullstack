package com.edutech.Edutech.controller;

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.model.Estudiante;
import com.edutech.Edutech.model.Inscripcion;
import com.edutech.Edutech.service.CursoService;
import com.edutech.Edutech.service.EstudianteService;
import com.edutech.Edutech.service.InscripcionService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InscripcionController.class)
@Import(HypermediaAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class InscripcionControllerHateoasTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InscripcionService insService;
    @MockBean
    private EstudianteService estudianteService;
    @MockBean
    private CursoService cursoService;

    @Test
    @DisplayName("GET /api/inscripciones/hateoas → colección HATEOAS")
    void listInscripcionesHateoas() throws Exception {
        // — datos de ejemplo
        Estudiante e = new Estudiante(); e.setId(1L); e.setNombre("X");
        Curso      c = new Curso();      c.setId(2L); c.setNombre("Y");
        Inscripcion i1 = new Inscripcion();
        i1.setId(10L); i1.setEstudiante(e); i1.setCurso(c); i1.setFechaInscripcion(LocalDate.now());
        Inscripcion i2 = new Inscripcion();
        i2.setId(11L); i2.setEstudiante(e); i2.setCurso(c); i2.setFechaInscripcion(LocalDate.now());
        given(insService.listarTodos()).willReturn(List.of(i1, i2));

        mvc.perform(get("/api/inscripciones/hateoas")
                .accept(MediaTypes.HAL_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaTypes.HAL_JSON))
           .andExpect(jsonPath("_embedded.inscripcionList[0].id").value(10))
           .andExpect(jsonPath("_embedded.inscripcionList[1].id").value(11))
           // ahora esperamos sólo el path
           .andExpect(jsonPath("_links.self.href")
               .value(linkTo(InscripcionController.class)
                   .slash("hateoas")
                   .toUri()
                   .getPath()));
    }

    @Test
    @DisplayName("GET /api/inscripciones/{id}/hateoas → recurso HATEOAS individual")
    void getInscripcionByIdHateoas() throws Exception {
        // — datos de ejemplo
        Estudiante e = new Estudiante(); e.setId(5L); e.setNombre("A");
        Curso      c = new Curso();      c.setId(6L); c.setNombre("B");
        Inscripcion i = new Inscripcion();
        i.setId(20L); i.setEstudiante(e); i.setCurso(c); i.setFechaInscripcion(LocalDate.now());
        given(insService.buscarPorId(20L)).willReturn(Optional.of(i));

        mvc.perform(get("/api/inscripciones/20/hateoas")
                .accept(MediaTypes.HAL_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaTypes.HAL_JSON))
           .andExpect(jsonPath("id").value(20))
           // self → path sólo
           .andExpect(jsonPath("_links.self.href")
               .value(linkTo(InscripcionController.class)
                   .slash(20)
                   .slash("hateoas")
                   .toUri()
                   .getPath()))
           // enlace a todas-las-inscripciones → también sólo path
           .andExpect(jsonPath("_links.todas-las-inscripciones.href")
               .value(linkTo(InscripcionController.class)
                   .slash("hateoas")
                   .toUri()
                   .getPath()));
    }
}
