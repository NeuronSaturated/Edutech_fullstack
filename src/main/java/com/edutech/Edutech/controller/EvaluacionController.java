package com.edutech.Edutech.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.Edutech.dto.EvaluacionConEstudianteDTO;
import com.edutech.Edutech.model.Evaluacion;
import com.edutech.Edutech.service.EvaluacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// —————————————————————————————————————————————————————————————

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService service;

    /*@PostMapping("/lote")
    public ResponseEntity<?> guardarEvaluaciones(@RequestBody List<Evaluacion> evaluaciones) {
        try {
            service.guardarEvaluaciones(evaluaciones);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar evaluaciones");
        }
    }*/

    // ————————————————————————————————————————————
    @Operation(
      summary     = "Guardar evaluaciones en lote",
      description = "Recibe una lista de evaluaciones y las guarda todas. Valida que cada evaluación tenga curso y estudiante con ID."
    )
    @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Evaluaciones guardadas correctamente"),
      @ApiResponse(responseCode = "400", description = "Faltan datos de curso o estudiante"),
      @ApiResponse(
        responseCode = "500",
        description = "Error interno al guardar",
        content     = @Content(mediaType = "text/plain")
      )
    })
    @PostMapping("/lote")
    public ResponseEntity<?> guardarEvaluaciones(@RequestBody List<Evaluacion> evaluaciones) {
        try {
            for (Evaluacion eval : evaluaciones) {
                if (eval.getCurso() == null || eval.getCurso().getId() == null ||
                    eval.getEstudiante() == null || eval.getEstudiante().getId() == null) {
                    return ResponseEntity.badRequest().body("Faltan datos de curso o estudiante");
                }
            }
            service.guardarEvaluaciones(evaluaciones);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al guardar evaluaciones");
        }
    }
    // ————————————————————————————————————————————

    // GET /api/evaluaciones
    // Devuelve la lista de todas las evaluaciones
    // ————————————————————————————————————————————
    @Operation(summary = "Listar todas las evaluaciones",
               description = "Devuelve la lista de todas las evaluaciones registradas")
    @ApiResponse(responseCode = "200",
                 description = "Listado de evaluaciones",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Evaluacion.class, type = "array")))
    @GetMapping
    public List<Evaluacion> listar() {
        return service.listarTodos();
    }
    // ————————————————————————————————————————————

    // GET /api/evaluaciones/{id}
    // Busca una evaluación por su id y devuelve 200 con la evaluación o 404 si no existe
    // ————————————————————————————————————————————
    @Operation(summary = "Buscar evaluación por ID",
               description = "Devuelve la evaluación si existe o 404 si no la encuentra")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Evaluación encontrada",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Evaluacion.class))),
      @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Evaluacion> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // ————————————————————————————————————————————

    // POST /api/evaluaciones
    // Crea una nueva evaluación con los datos enviados en el cuerpo JSON
    // ————————————————————————————————————————————
    @Operation(summary = "Crear evaluación",
               description = "Crea una nueva evaluación con los datos proporcionados")
    @ApiResponse(responseCode = "200",
                 description = "Evaluación creada correctamente",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Evaluacion.class)))
    @PostMapping
    public Evaluacion crear(@RequestBody Evaluacion evaluacion) {
        return service.guardar(evaluacion);
    }
    // ————————————————————————————————————————————

    // PUT /api/evaluaciones/{id}
    // Actualiza una evaluación existente identificado por id con los datos del cuerpo JSON
    // ————————————————————————————————————————————
    @Operation(summary = "Actualizar evaluación",
               description = "Modifica una evaluación existente identificado por ID")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Evaluación actualizada",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Evaluacion.class))),
      @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @PutMapping("/uno/{id}")
    public ResponseEntity<Evaluacion> actualizar(@PathVariable Long id, @RequestBody Evaluacion evaluacion) {
        return service.actualizar(id, evaluacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // ————————————————————————————————————————————

    // DELETE /api/evaluaciones/{id}
    // Elimina una evaluación por id, responde 204 No Content si fue eliminada, o 404 si no existe
    // ————————————————————————————————————————————
    @Operation(summary = "Eliminar evaluación",
               description = "Borra una evaluación existente por ID")
    @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Evaluación eliminada"),
      @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    // ————————————————————————————————————————————

    // GET /api/evaluaciones/curso/{idCurso}
    // Devuelve la lista de evaluaciones con datos de estudiante (DTO)
    // ————————————————————————————————————————————
    @Operation(summary = "Listar evaluaciones por curso",
               description = "Devuelve las evaluaciones de un curso junto con datos del estudiante")
    @ApiResponse(responseCode = "200",
                 description = "Listado de EvaluacionConEstudianteDTO",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EvaluacionConEstudianteDTO.class, type = "array")))
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<EvaluacionConEstudianteDTO>> obtenerEvaluacionesPorCurso(@PathVariable Long idCurso) {
        List<EvaluacionConEstudianteDTO> lista = service.obtenerEvaluacionesPorCurso(idCurso);
        return ResponseEntity.ok(lista);
    }
    // ————————————————————————————————————————————

    // PUT /api/evaluaciones/actualizar
    // Actualiza múltiples evaluaciones en lote
    // ————————————————————————————————————————————
    @Operation(summary = "Actualizar múltiples evaluaciones",
               description = "Recibe y actualiza en lote una lista de evaluaciones existentes")
    @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Evaluaciones actualizadas correctamente"),
      @ApiResponse(
        responseCode = "500",
        description = "Error interno al actualizar",
        content     = @Content(mediaType = "text/plain")
      )
    })
    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarEvaluaciones(@RequestBody List<Evaluacion> evaluaciones) {
        try {
            service.actualizarEvaluacionesEnLote(evaluaciones);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar evaluaciones");
        }
    }
    // ————————————————————————————————————————————

    // —————————————————————————————————————————————————————————————
    // BLOQUE EXTRA HATEOAS: rutas adicionales para cumplir HATEOAS
    // —————————————————————————————————————————————————————————————

    /**
     * —[HATEOAS]— Listar todas las evaluaciones con enlaces
     * Cada Evaluacion lleva un enlace “self” y la colección un enlace “self”
     */
    @Operation(
      summary     = "Listar evaluaciones con HATEOAS",
      description = "Devuelve una colección de evaluaciones con enlaces self en cada recurso y en la colección"
    )
    @ApiResponse(
      responseCode = "200",
      description = "Evaluaciones con enlaces HATEOAS",
      content     = @Content(mediaType = "application/hal+json",
                             schema = @Schema(implementation = Evaluacion.class, type = "array"))
    )
    @GetMapping("/hateoas")
    public ResponseEntity<CollectionModel<EntityModel<Evaluacion>>> listarHateoas() {
        var modelos = service.listarTodos().stream()
            .map(ev -> {
                EntityModel<Evaluacion> m = EntityModel.of(ev);
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EvaluacionController.class)
                        .buscar(ev.getId())
                ).withSelfRel());
                return m;
            })
            .toList();
        CollectionModel<EntityModel<Evaluacion>> coleccion = CollectionModel.of(modelos);
        coleccion.add(WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(EvaluacionController.class)
                .listarHateoas()
        ).withSelfRel());
        return ResponseEntity.ok(coleccion);
    }

    /**
     * —[HATEOAS]— Buscar evaluación por ID con enlaces
     * Añade enlace “self” y enlace “todas-las-evaluaciones”
     */
    @Operation(
      summary     = "Buscar evaluación con HATEOAS",
      description = "Devuelve una evaluación por ID con enlaces self y colección"
    )
    @ApiResponses({
      @ApiResponse(
        responseCode = "200",
        description  = "Evaluación encontrada con enlaces",
        content      = @Content(mediaType = "application/hal+json",
                               schema = @Schema(implementation = Evaluacion.class))
      ),
      @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @GetMapping("/{id}/hateoas")
    public ResponseEntity<EntityModel<Evaluacion>> buscarHateoas(@PathVariable Long id) {
        return service.buscarPorId(id)
            .map(ev -> {
                EntityModel<Evaluacion> m = EntityModel.of(ev);
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EvaluacionController.class)
                        .buscarHateoas(id)
                ).withSelfRel());
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EvaluacionController.class)
                        .listarHateoas()
                ).withRel("todas-las-evaluaciones"));
                return ResponseEntity.ok(m);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    // —————————————————————————————————————————————————————————————

}
