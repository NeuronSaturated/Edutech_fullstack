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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InscripcionController.class)
@AutoConfigureMockMvc(addFilters = false)
class InscripcionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private InscripcionService service;

    @MockBean
    private EstudianteService estudianteService;

    @MockBean
    private CursoService cursoService;

    @Test
    @DisplayName("GET /api/inscripciones → 200 y lista de inscripciones")
    void whenGetAll_thenReturnList() throws Exception {
        Inscripcion i1 = new Inscripcion(); i1.setId(1L);
        Inscripcion i2 = new Inscripcion(); i2.setId(2L);

        given(service.listarTodos()).willReturn(List.of(i1, i2));

        mvc.perform(get("/api/inscripciones"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].id").value(1))
           .andExpect(jsonPath("$[1].id").value(2));

        verify(service).listarTodos();
    }

    @Test
    @DisplayName("GET /api/inscripciones/{id} → 200 si existe")
    void whenGetByIdExists_then200() throws Exception {
        Inscripcion i = new Inscripcion();
        i.setId(5L);
        given(service.buscarPorId(5L)).willReturn(Optional.of(i));

        mvc.perform(get("/api/inscripciones/5"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(5));

        verify(service).buscarPorId(5L);
    }

    @Test
    @DisplayName("GET /api/inscripciones/{id} → 404 si no existe")
    void whenGetByIdNotFound_then404() throws Exception {
        given(service.buscarPorId(99L)).willReturn(Optional.empty());

        mvc.perform(get("/api/inscripciones/99"))
           .andExpect(status().isNotFound());

        verify(service).buscarPorId(99L);
    }

    @Test
    @DisplayName("POST /api/inscripciones → 200 crea inscripcion (payload)")
    void whenPostBody_thenCreateInscripcion() throws Exception {
        String payload = """
            {
              "estudiante": {"id":5},
              "curso":      {"id":7}
            }
            """;

        Inscripcion saved = new Inscripcion();
        saved.setId(42L);

        given(estudianteService.buscarPorId(5L)).willReturn(Optional.of(new Estudiante()));
        given(cursoService.buscarPorId(7L)).willReturn(Optional.of(new Curso()));
        given(service.inscribirEstudiante(5L, 7L)).willReturn(Optional.of(saved));

        mvc.perform(post("/api/inscripciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(42));

        verify(service).inscribirEstudiante(5L, 7L);
    }

    @Test
    @DisplayName("POST /api/inscripciones/estudiante/{e}/curso/{c} → 200 crea inscripcion")
    void whenPostPathParams_thenCreateInscripcion() throws Exception {
        Inscripcion saved = new Inscripcion();
        saved.setId(55L);

        given(estudianteService.buscarPorId(2L)).willReturn(Optional.of(new Estudiante()));
        given(cursoService.buscarPorId(3L)).willReturn(Optional.of(new Curso()));
        given(service.inscribirEstudiante(2L, 3L)).willReturn(Optional.of(saved));

        mvc.perform(post("/api/inscripciones/estudiante/2/curso/3"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(55));

        verify(service).inscribirEstudiante(2L, 3L);
    }

    @Test
    @DisplayName("PUT /api/inscripciones/{id} → 200 actualiza inscripcion")
    void whenPut_thenUpdate() throws Exception {
        String body = """
            { "estudiante": {"id":8}, "curso": {"id":9} }
            """;
        Inscripcion updated = new Inscripcion();
        updated.setId(10L);

        given(service.actualizar(eq(10L), any(Inscripcion.class)))
            .willReturn(Optional.of(updated));

        mvc.perform(put("/api/inscripciones/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(10));

        verify(service).actualizar(eq(10L), any(Inscripcion.class));
    }

    @Test
    @DisplayName("DELETE /api/inscripciones/{id} → 204 si existe")
    void whenDeleteExists_then204() throws Exception {
        given(service.eliminar(7L)).willReturn(true);

        mvc.perform(delete("/api/inscripciones/7"))
           .andExpect(status().isNoContent());

        verify(service).eliminar(7L);
    }

    @Test
    @DisplayName("DELETE /api/inscripciones/{id} → 404 si no existe")
    void whenDeleteNotExists_then404() throws Exception {
        given(service.eliminar(8L)).willReturn(false);

        mvc.perform(delete("/api/inscripciones/8"))
           .andExpect(status().isNotFound());

        verify(service).eliminar(8L);
    }
}
