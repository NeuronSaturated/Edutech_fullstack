package com.edutech.Edutech.controller;

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
    private InscripcionService inscripcionService;

    @MockBean
    private CursoService cursoService;           
    @MockBean
    private EstudianteService estudianteService; 

    @Test
    @DisplayName("GET /api/inscripciones/hateoas → colección HATEOAS")
    void listaHateoas() throws Exception {
        Inscripcion i1 = new Inscripcion(); i1.setId(1L); i1.setFechaInscripcion(LocalDate.of(2025,1,1));
        Inscripcion i2 = new Inscripcion(); i2.setId(2L); i2.setFechaInscripcion(LocalDate.of(2025,2,2));

        given(inscripcionService.listarTodos()).willReturn(List.of(i1,i2));

        mvc.perform(get("/api/inscripciones/hateoas").accept(MediaTypes.HAL_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/hal+json"))
           // el rel por defecto será "inscripcionList" (clase Inscripcion → nombre minuscula + List)
           .andExpect(jsonPath("_embedded.inscripcionList[0].id").value(1))
           .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/inscripciones/{id}/hateoas → recurso HATEOAS individual")
    void porIdHateoas() throws Exception {
        Inscripcion i = new Inscripcion();
        i.setId(5L);
        i.setFechaInscripcion(LocalDate.of(2025,5,5));

        given(inscripcionService.buscarPorId(5L)).willReturn(Optional.of(i));

        mvc.perform(get("/api/inscripciones/5/hateoas").accept(MediaTypes.HAL_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/hal+json"))
           .andExpect(jsonPath("id").value(5))
           .andExpect(jsonPath("_links.self.href")
                   .value(linkTo(InscripcionController.class).slash(5).toUri().toString()))
           .andExpect(jsonPath("_links.todas-las-inscripciones.href").exists());
    }
}
