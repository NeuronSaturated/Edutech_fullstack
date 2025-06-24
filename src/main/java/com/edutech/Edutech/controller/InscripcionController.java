package com.edutech.Edutech.controller;

import java.util.List;
import java.util.Optional;

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

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.model.Estudiante;
import com.edutech.Edutech.model.Inscripcion;
import com.edutech.Edutech.service.CursoService;
import com.edutech.Edutech.service.EstudianteService;
import com.edutech.Edutech.service.InscripcionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// —————————————————————————————————————————————————————————————

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService service;

    // —————————————————————————————————————————————————————————————
    // Servicios para validación previa
    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private CursoService cursoService;
    // —————————————————————————————————————————————————————————————

    // Lista todas las inscripciones
    // ————————————————————————————————————————————
    @Operation(summary = "Listar todas las inscripciones",
               description = "Devuelve todas las inscripciones registradas")
    @ApiResponse(responseCode = "200",
                 description = "Listado de inscripciones",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Inscripcion.class, type = "array")))
    // ————————————————————————————————————————————
    @GetMapping
    public List<Inscripcion> listar() {
        return service.listarTodos();
    }

    // Buscar una inscripción por ID
    // ————————————————————————————————————————————
    @Operation(summary = "Buscar inscripción por ID",
               description = "Devuelve la inscripción si existe o 404 si no la encuentra")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Inscripción encontrada",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Inscripcion.class))),
      @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    // ————————————————————————————————————————————
    @GetMapping("/{id}")
    public ResponseEntity<Inscripcion> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Inscribir estudiante por URL con IDs
    // ————————————————————————————————————————————
    @Operation(summary = "Inscribir estudiante en un curso",
               description = "Crea una inscripción usando los IDs de estudiante y curso")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Inscripción creada",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Inscripcion.class))),
      @ApiResponse(responseCode = "404", description = "Estudiante o curso no encontrado"),
      @ApiResponse(responseCode = "409", description = "Ya existe la inscripción")
    })
    // ————————————————————————————————————————————
    @PostMapping("/estudiante/{idEstudiante}/curso/{idCurso}")
    public ResponseEntity<?> inscribir(@PathVariable Long idEstudiante, @PathVariable Long idCurso) {
        // ————————————————————————————————————————————
        // Validación previa: existe el estudiante?
        if (estudianteService.buscarPorId(idEstudiante).isEmpty()) {
            return ResponseEntity.status(404).body("Estudiante no encontrado");
        }
        // Validación previa: existe el curso?
        if (cursoService.buscarPorId(idCurso).isEmpty()) {
            return ResponseEntity.status(404).body("Curso no encontrado");
        }
        // ————————————————————————————————————————————

        Optional<Inscripcion> resultado = service.inscribirEstudiante(idEstudiante, idCurso);
        if (resultado.isPresent()) {
            return ResponseEntity.ok(resultado.get());
        } else {
            return ResponseEntity.status(409).body("El estudiante ya está inscrito en este curso.");
        }
    }

    // Inscripción usando objeto Inscripcion con estudiante y curso embebidos
    // ————————————————————————————————————————————
    @Operation(summary = "Inscribir estudiante (payload)",
               description = "Crea una inscripción usando un objeto Inscripcion en el cuerpo")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Inscripción creada",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Inscripcion.class))),
      @ApiResponse(responseCode = "404", description = "Estudiante o curso no encontrado"),
      @ApiResponse(responseCode = "409", description = "Ya existe la inscripción")
    })
    // ————————————————————————————————————————————
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Inscripcion inscripcion) {
        Long idEstudiante = inscripcion.getEstudiante().getId();
        Long idCurso      = inscripcion.getCurso().getId();

        // ————————————————————————————————————————————
        // Validación previa: existe el estudiante?
        if (estudianteService.buscarPorId(idEstudiante).isEmpty()) {
            return ResponseEntity.status(404).body("Estudiante no encontrado");
        }
        // Validación previa: existe el curso?
        if (cursoService.buscarPorId(idCurso).isEmpty()) {
            return ResponseEntity.status(404).body("Curso no encontrado");
        }
        // ————————————————————————————————————————————

        Optional<Inscripcion> resultado = service.inscribirEstudiante(idEstudiante, idCurso);
        if (resultado.isPresent()) {
            return ResponseEntity.ok(resultado.get());
        } else {
            return ResponseEntity.status(409).body("El estudiante ya está inscrito en este curso.");
        }
    }

    // Actualizar inscripción por ID
    // ————————————————————————————————————————————
    @Operation(summary = "Actualizar inscripción",
               description = "Modifica una inscripción existente por ID")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Inscripción actualizada",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Inscripcion.class))),
      @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    // ————————————————————————————————————————————
    @PutMapping("/{id}")
    public ResponseEntity<Inscripcion> actualizar(@PathVariable Long id, @RequestBody Inscripcion inscripcion) {
        return service.actualizar(id, inscripcion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar inscripción
    // ————————————————————————————————————————————
    @Operation(summary = "Eliminar inscripción",
               description = "Borra una inscripción existente por ID")
    @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Inscripción eliminada"),
      @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    // ————————————————————————————————————————————
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Listar cursos por estudiante
    // ————————————————————————————————————————————
    @Operation(summary = "Listar cursos de un estudiante",
               description = "Devuelve los cursos a los que está inscrito un estudiante")
    @ApiResponse(responseCode = "200",
                 description = "Listado de cursos",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Curso.class, type = "array")))
    // ————————————————————————————————————————————
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<Curso>> listarCursosPorEstudiante(@PathVariable Long idEstudiante) {
        List<Curso> cursos = service.listarCursosPorEstudiante(idEstudiante);
        return ResponseEntity.ok(cursos);
    }

    // Listar estudiantes por curso
    // ————————————————————————————————————————————
    @Operation(summary = "Listar estudiantes de un curso",
               description = "Devuelve los estudiantes inscritos en un curso")
    @ApiResponse(responseCode = "200",
                 description = "Listado de estudiantes",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Estudiante.class, type = "array")))
    // ————————————————————————————————————————————
    @GetMapping("/curso/{idCurso}/estudiantes")
    public ResponseEntity<List<Estudiante>> obtenerEstudiantesPorCurso(@PathVariable Long idCurso) {
        List<Estudiante> estudiantes = service.obtenerEstudiantesPorCursoId(idCurso);
        return ResponseEntity.ok(estudiantes);
    }

    // —————————————————————————————————————————————————————————————
    // BLOQUE EXTRA HATEOAS: rutas adicionales para cumplir HATEOAS
    // —————————————————————————————————————————————————————————————

    /**
     * —[HATEOAS]— Listar todas las inscripciones con enlaces
     * Cada Inscripcion lleva un enlace “self” y la colección un enlace “self”
     */
    @GetMapping("/hateoas")
    public ResponseEntity<CollectionModel<EntityModel<Inscripcion>>> listarHateoas() {
        var modelos = service.listarTodos().stream()
            .map(i -> {
                EntityModel<Inscripcion> m = EntityModel.of(i);
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(InscripcionController.class)
                        .buscarHateoas(i.getId())
                ).withSelfRel());
                return m;
            }).toList();
        CollectionModel<EntityModel<Inscripcion>> coleccion = CollectionModel.of(modelos);
        coleccion.add(WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(InscripcionController.class)
                .listarHateoas()
        ).withSelfRel());
        return ResponseEntity.ok(coleccion);
    }

    /**
     * —[HATEOAS]— Buscar inscripción por ID con enlaces
     * Añade enlace “self” y enlace “todas-las-inscripciones”
     */
    @GetMapping("/{id}/hateoas")
    public ResponseEntity<EntityModel<Inscripcion>> buscarHateoas(@PathVariable Long id) {
        return service.buscarPorId(id)
            .map(i -> {
                EntityModel<Inscripcion> m = EntityModel.of(i);
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(InscripcionController.class)
                        .buscarHateoas(id)
                ).withSelfRel());
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(InscripcionController.class)
                        .listarHateoas()
                ).withRel("todas-las-inscripciones"));
                return ResponseEntity.ok(m);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    // —————————————————————————————————————————————————————————————

}
