package com.edutech.Edutech.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.Edutech.service.CursoService;
import com.edutech.Edutech.service.EstudianteService;
import com.edutech.Edutech.service.InscripcionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private EstudianteService estudianteService;

    // ————————————————————————————————————————————
    @Operation(
      summary     = "Conteo de estudiantes inscritos en un curso",
      description = "Devuelve el número de estudiantes inscritos en el curso indicado"
    )
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Conteo calculado",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Map.class))),
      @ApiResponse(responseCode = "404",
                   description = "Curso no encontrado")
    })
    @GetMapping("/estudiantes-por-curso/{idCurso}")
    public ResponseEntity<EntityModel<Map<String,Object>>> conteoEstudiantesPorCurso(
            @PathVariable Long idCurso) {

        return cursoService.buscarPorId(idCurso)
            .map(curso -> {
                long total = inscripcionService
                    .obtenerEstudiantesPorCursoId(idCurso)
                    .size();

                Map<String,Object> body = Map.of(
                    "idCurso", curso.getId(),
                    "nombreCurso", curso.getNombre(),
                    "totalEstudiantes", total
                );

                EntityModel<Map<String,Object>> model = EntityModel.of(body);
                model.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ReporteController.class)
                        .conteoEstudiantesPorCurso(idCurso)
                ).withSelfRel());

                return ResponseEntity.ok(model);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    // ————————————————————————————————————————————

    // ————————————————————————————————————————————
    @Operation(
      summary     = "Conteo de cursos en que está inscrito un estudiante",
      description = "Devuelve el número de cursos en los que está inscrito el estudiante indicado"
    )
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Conteo calculado",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Map.class))),
      @ApiResponse(responseCode = "404",
                   description = "Estudiante no encontrado")
    })
    @GetMapping("/cursos-por-estudiante/{idEstudiante}")
    public ResponseEntity<EntityModel<Map<String,Object>>> conteoCursosPorEstudiante(
            @PathVariable Long idEstudiante) {

        return estudianteService.buscarPorId(idEstudiante)
            .map(est -> {
                long total = inscripcionService
                    .listarCursosPorEstudiante(idEstudiante)
                    .size();

                Map<String,Object> body = Map.of(
                    "idEstudiante", est.getId(),
                    "nombreEstudiante", est.getNombre(),
                    "totalCursos", total
                );

                EntityModel<Map<String,Object>> model = EntityModel.of(body);
                model.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ReporteController.class)
                        .conteoCursosPorEstudiante(idEstudiante)
                ).withSelfRel());

                return ResponseEntity.ok(model);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    // ————————————————————————————————————————————

    // —————————————————————————————————————————————————————————————
    // BLOQUE EXTRA HATEOAS: endpoints “puros” para que aparezcan con /hateoas
    // —————————————————————————————————————————————————————————————

    @Operation(
      summary     = "[HATEOAS] Conteo de estudiantes inscritos (enlace self)",
      description = "Mismo reporte que /estudiantes-por-curso, pero con sufijo /hateoas"
    )
    @ApiResponse(responseCode = "200", description = "Modelo HATEOAS retornado")
    @GetMapping("/estudiantes-por-curso/{idCurso}/hateoas")
    public ResponseEntity<EntityModel<Map<String,Object>>> conteoEstudiantesPorCursoHateoas(
            @PathVariable Long idCurso) {
        // delegamos al método normal (que ya construye el EntityModel con enlace)
        return conteoEstudiantesPorCurso(idCurso);
    }

    @Operation(
      summary     = "[HATEOAS] Conteo de cursos por estudiante (enlace self)",
      description = "Mismo reporte que /cursos-por-estudiante, pero con sufijo /hateoas"
    )
    @ApiResponse(responseCode = "200", description = "Modelo HATEOAS retornado")
    @GetMapping("/cursos-por-estudiante/{idEstudiante}/hateoas")
    public ResponseEntity<EntityModel<Map<String,Object>>> conteoCursosPorEstudianteHateoas(
            @PathVariable Long idEstudiante) {
        return conteoCursosPorEstudiante(idEstudiante);
    }
    // —————————————————————————————————————————————————————————————

}
