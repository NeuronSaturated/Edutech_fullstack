package com.edutech.Edutech.controller;

import com.edutech.Edutech.model.Evaluacion;
import com.edutech.Edutech.service.EvaluacionService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EvaluacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvaluacionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EvaluacionService service;

    @Test
    void whenGetAll_thenReturnJsonArray() throws Exception {
        Evaluacion e1 = new Evaluacion(); e1.setId(1L);
        Evaluacion e2 = new Evaluacion(); e2.setId(2L);

        given(service.listarTodos()).willReturn(List.of(e1, e2));

        mvc.perform(get("/api/evaluaciones")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].id").value(1))
           .andExpect(jsonPath("$[1].id").value(2));

        verify(service, times(1)).listarTodos();
    }

    @Test
    void whenGetByIdExists_thenReturnEvaluacion() throws Exception {
        Evaluacion e = new Evaluacion(); e.setId(5L);
        given(service.buscarPorId(5L)).willReturn(Optional.of(e));

        mvc.perform(get("/api/evaluaciones/5")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void whenGetByIdNotFound_then404() throws Exception {
        given(service.buscarPorId(99L)).willReturn(Optional.empty());

        mvc.perform(get("/api/evaluaciones/99")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void whenPost_thenCreateEvaluacion() throws Exception {
        Evaluacion in = new Evaluacion();
        String payload = "{}"; // completa campos si los validas
        given(service.guardar(any(Evaluacion.class))).willReturn(in);

        mvc.perform(post("/api/evaluaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
           .andExpect(status().isOk());
    }

    @Test
    void whenPut_thenUpdateEvaluacion() throws Exception {
        Evaluacion updated = new Evaluacion(); updated.setId(3L);
        given(service.actualizar(eq(3L), any(Evaluacion.class))).willReturn(Optional.of(updated));

        mvc.perform(put("/api/evaluaciones/uno/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void whenDelete_then204() throws Exception {
        given(service.eliminar(4L)).willReturn(true);

        mvc.perform(delete("/api/evaluaciones/4"))
           .andExpect(status().isNoContent());
    }

    @Test
    void whenGetByCurso_thenReturnDtoList() throws Exception {
        // Asume que tu DTO serializa campo "idEstudiante"
        given(service.obtenerEvaluacionesPorCurso(7L))
            .willReturn(List.of(/* EvaluacionConEstudianteDTO objects */));

        mvc.perform(get("/api/evaluaciones/curso/7")
                .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk());
    }
}
