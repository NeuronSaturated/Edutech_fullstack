package com.edutech.Edutech.controller;

import com.edutech.Edutech.model.Inscripcion;
import com.edutech.Edutech.service.CursoService;
import com.edutech.Edutech.service.EstudianteService;
import com.edutech.Edutech.service.InscripcionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InscripcionController.class)
@AutoConfigureMockMvc(addFilters = false)
class InscripcionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InscripcionService inscripcionService;

    @MockBean
    private CursoService cursoService;            // << añadidos
    @MockBean
    private EstudianteService estudianteService;  // << añadidos

    @Test
    void whenGetAll_thenReturnJsonArray() throws Exception {
        Inscripcion i1 = new Inscripcion();
        i1.setId(1L);
        i1.setFechaInscripcion(LocalDate.of(2025,1,1));
        Inscripcion i2 = new Inscripcion();
        i2.setId(2L);
        i2.setFechaInscripcion(LocalDate.of(2025,2,2));

        given(inscripcionService.listarTodos()).willReturn(List.of(i1, i2));

        mvc.perform(get("/api/inscripciones"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].id").value(1))
           .andExpect(jsonPath("$[1].fechaInscripcion").value("2025-02-02"));
    }

    @Test
    void whenGetByIdExists_thenReturnOne() throws Exception {
        Inscripcion i = new Inscripcion();
        i.setId(5L);
        i.setFechaInscripcion(LocalDate.of(2025,5,5));

        given(inscripcionService.buscarPorId(5L)).willReturn(Optional.of(i));

        mvc.perform(get("/api/inscripciones/5")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(5))
           .andExpect(jsonPath("$.fechaInscripcion").value("2025-05-05"));
    }

    @Test
    void whenGetByIdNotFound_then404() throws Exception {
        given(inscripcionService.buscarPorId(99L)).willReturn(Optional.empty());

        mvc.perform(get("/api/inscripciones/99"))
           .andExpect(status().isNotFound());
    }

    @Test
    void whenPost_thenCreateInscripcion() throws Exception {
        String payload = """
            {
              "estudiante": {"id":3},
              "curso": {"id":4}
            }
            """;
        Inscripcion saved = new Inscripcion();
        saved.setId(10L);
        saved.setFechaInscripcion(LocalDate.now());
        given(inscripcionService.inscribirEstudiante(3L,4L))
             .willReturn(Optional.of(saved));

        mvc.perform(post("/api/inscripciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", "/api/inscripciones/10"))
           .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void whenDelete_then204() throws Exception {
        given(inscripcionService.eliminar(7L)).willReturn(true);

        mvc.perform(delete("/api/inscripciones/7"))
           .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteNonexistent_then404() throws Exception {
        given(inscripcionService.eliminar(8L)).willReturn(false);

        mvc.perform(delete("/api/inscripciones/8"))
           .andExpect(status().isNotFound());
    }
}
