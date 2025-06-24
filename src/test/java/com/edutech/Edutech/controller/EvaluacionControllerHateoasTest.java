package com.edutech.Edutech.controller;

import com.edutech.Edutech.model.Evaluacion;
import com.edutech.Edutech.service.EvaluacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvaluacionControllerHateoasTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EvaluacionService service;

    @Test
    @DisplayName("GET /api/evaluaciones/hateoas → colección HATEOAS")
    void listarHateoas() throws Exception {
        Evaluacion e1 = new Evaluacion(); e1.setId(1L);
        Evaluacion e2 = new Evaluacion(); e2.setId(2L);

        given(service.listarTodos()).willReturn(List.of(e1, e2));

        mvc.perform(get("/api/evaluaciones/hateoas").accept(MediaTypes.HAL_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaTypes.HAL_JSON))
           // rel por defecto suele ser 'evaluacionList'
           .andExpect(jsonPath("_embedded.evaluacionList[0].id").value(1))
           .andExpect(jsonPath("_embedded.evaluacionList[1].id").value(2))
           .andExpect(jsonPath("_links.self.href").exists());
    }

    @Test
    @DisplayName("GET /api/evaluaciones/{id}/hateoas → recurso HATEOAS individual")
    void buscarHateoas_porId() throws Exception {
        Evaluacion e = new Evaluacion(); e.setId(5L);
        given(service.buscarPorId(5L)).willReturn(Optional.of(e));

        mvc.perform(get("/api/evaluaciones/5/hateoas").accept(MediaTypes.HAL_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaTypes.HAL_JSON))
           .andExpect(jsonPath("id").value(5))
           .andExpect(jsonPath("_links.self.href").exists())
           .andExpect(jsonPath("_links.todas-las-evaluaciones.href").exists());
    }
}
