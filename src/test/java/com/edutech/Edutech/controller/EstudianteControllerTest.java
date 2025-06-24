package com.edutech.Edutech.controller;

import com.edutech.Edutech.model.Estudiante;
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

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EstudianteController.class)
@AutoConfigureMockMvc(addFilters = false)
class EstudianteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EstudianteService service;

    // Mockea también la dependencia que usa tu controlador
    @MockBean
    private InscripcionService inscripcionService;

    @Test
    @DisplayName("GET /api/estudiantes → lista de estudiantes")
    void whenGetAll_thenReturnJsonArray() throws Exception {
        Estudiante e1 = new Estudiante(); e1.setId(1L); e1.setNombre("Juan"); e1.setCarrera("Sistemas"); e1.setCorreo("juan@mail.com"); e1.setPassword("pwd"); e1.setRol("ESTUDIANTE");
        Estudiante e2 = new Estudiante(); e2.setId(2L); e2.setNombre("María"); e2.setCarrera("Derecho"); e2.setCorreo("maria@mail.com"); e2.setPassword("pwd"); e2.setRol("ESTUDIANTE");

        given(service.listarTodos()).willReturn(Arrays.asList(e1, e2));

        mvc.perform(get("/api/estudiantes")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].nombre").value("Juan"))
           .andExpect(jsonPath("$[1].correo").value("maria@mail.com"));
    }

    @Test
    @DisplayName("GET /api/estudiantes/{id} → estudiante existente")
    void whenGetByIdExists_thenReturnEstudiante() throws Exception {
        Estudiante e = new Estudiante(); e.setId(1L); e.setNombre("Ana"); e.setCarrera("Psicología"); e.setCorreo("ana@mail.com"); e.setPassword("pwd"); e.setRol("ESTUDIANTE");
        given(service.buscarPorId(1L)).willReturn(Optional.of(e));

        mvc.perform(get("/api/estudiantes/1")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    @DisplayName("GET /api/estudiantes/{id} → 404 si no existe")
    void whenGetByIdNotFound_then404() throws Exception {
        given(service.buscarPorId(999L)).willReturn(Optional.empty());

        mvc.perform(get("/api/estudiantes/999")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/estudiantes → crea un estudiante")
    void whenPost_thenCreateEstudiante() throws Exception {
        Estudiante nuevo = new Estudiante(); nuevo.setNombre("Luis"); nuevo.setCarrera("Arte"); nuevo.setCorreo("luis@mail.com"); nuevo.setPassword("pwd"); nuevo.setRol("ESTUDIANTE");
        Estudiante creado = new Estudiante(); creado.setId(10L); creado.setNombre("Luis"); creado.setCarrera("Arte"); creado.setCorreo("luis@mail.com"); creado.setPassword("pwd"); creado.setRol("ESTUDIANTE");

        given(service.guardar(any(Estudiante.class))).willReturn(creado);

        mvc.perform(post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre":"Luis",
                      "carrera":"Arte",
                      "correo":"luis@mail.com",
                      "password":"pwd",
                      "rol":"ESTUDIANTE"
                    }
                    """))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", "/api/estudiantes/10"))
           .andExpect(jsonPath("$.id").value(10))
           .andExpect(jsonPath("$.nombre").value("Luis"));
    }

    @Test
    @DisplayName("PUT /api/estudiantes/{id} → actualiza un estudiante")
    void whenPut_thenUpdateEstudiante() throws Exception {
        Estudiante update = new Estudiante(); update.setId(5L); update.setNombre("Pedro"); update.setCarrera("Economía"); update.setCorreo("pedro@mail.com"); update.setPassword("pwd"); update.setRol("ESTUDIANTE");
        given(service.actualizar(eq(5L), any(Estudiante.class))).willReturn(Optional.of(update));

        mvc.perform(put("/api/estudiantes/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre":"Pedro",
                      "carrera":"Economía",
                      "correo":"pedro@mail.com",
                      "password":"pwd",
                      "rol":"ESTUDIANTE"
                    }
                    """))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.nombre").value("Pedro"));
    }

    @Test
    @DisplayName("DELETE /api/estudiantes/{id} → elimina un estudiante")
    void whenDelete_then204() throws Exception {
        given(service.eliminar(3L)).willReturn(true);

        mvc.perform(delete("/api/estudiantes/3"))
           .andExpect(status().isNoContent());
    }
}
