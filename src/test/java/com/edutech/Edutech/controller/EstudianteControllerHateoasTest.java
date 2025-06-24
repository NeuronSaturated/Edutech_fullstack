package com.edutech.Edutech.controller;

import com.edutech.Edutech.model.Estudiante;
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

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EstudianteController.class)
@Import(HypermediaAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class EstudianteControllerHateoasTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EstudianteService service;
    
    // Mockea también InscripcionService para que el contexto cargue
    @MockBean
    private InscripcionService inscripcionService;


    @Test
    @DisplayName("GET /api/estudiantes/{id} → HATEOAS links")
    void whenGetByIdHateoas_thenReturnWithLinks() throws Exception {
        Estudiante e = new Estudiante(); e.setId(5L); e.setNombre("Pablo"); e.setCarrera("Medicina"); e.setCorreo("pablo@mail.com"); e.setPassword("pwd"); e.setRol("ESTUDIANTE");
        given(service.buscarPorId(5L)).willReturn(Optional.of(e));

        mvc.perform(get("/api/estudiantes/5").accept(MediaTypes.HAL_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("_links.self.href")
                   .value(linkTo(EstudianteController.class).slash(5).toUri().toString()))
           .andExpect(jsonPath("_links.estudiantes.href").exists());
    }
}
