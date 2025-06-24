package com.edutech.Edutech.controller;

// Importas modelos Curso y Estudiante, además de servicios para manejarlos
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
import com.edutech.Edutech.service.CursoService;
import com.edutech.Edutech.service.EstudianteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

// —————————————————————————————————————————————————————————————

// Define que esta clase es un controlador REST y maneja solicitudes en /api/cursos
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    // Inyecta el servicio que gestiona lógica relacionada con cursos
    @Autowired
    private CursoService cursoService;

    // Inyecta el servicio que gestiona lógica relacionada con estudiantes (y docentes)
    @Autowired
    private EstudianteService estudianteService;

    // GET /api/cursos
    // Devuelve la lista completa de todos los cursos
    // —[OpenAPI]— Listar todos los cursos
    @Operation(summary = "Listar todos los cursos",
               description = "Obtiene la lista completa de cursos registrados")
    @ApiResponse(responseCode = "200",
                 description = "Listado de cursos",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Curso.class, type = "array")))
    // —————————————————————————————————————————————————————————————
    @GetMapping
    public List<Curso> listar() {
        return cursoService.listarTodos();
    }

    // GET /api/cursos/{id}
    // Busca un curso por su id y devuelve el curso si existe, o 404 si no
    // —————————————————————————————————————————————————————————————
    // —[OpenAPI]— Obtener curso por ID
    @Operation(summary = "Buscar curso por ID",
               description = "Devuelve un curso si existe, o 404 si no se encuentra")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Curso encontrado",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Curso.class))),
      @ApiResponse(responseCode = "404", description = "No se encontró el curso")
    })
    // —————————————————————————————————————————————————————————————
    @GetMapping("/{id}")
    public ResponseEntity<Curso> buscar(@PathVariable Long id) {
        return cursoService.buscarPorId(id)
                .map(ResponseEntity::ok) // si existe, devuelve 200 con el curso
                .orElse(ResponseEntity.notFound().build()); // si no, 404
    }

    // POST /api/cursos
    // Crea un nuevo curso con los datos que vienen en el cuerpo JSON
    // —————————————————————————————————————————————————————————————
    // —[OpenAPI]— Crear nuevo curso
    @Operation(summary = "Crear curso", description = "Registra un nuevo curso")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "JSON con los datos del curso",
        required = true,
        content = @Content(
          mediaType = "application/json",
          schema = @Schema(implementation = Curso.class),
          examples = @ExampleObject(
            name  = "ejemplo-curso",
            value = "{\"nombre\":\"Spring Avanzado\",\"descripcion\":\"Profundiza en Spring Security y HATEOAS\",\"duracionHoras\":32,\"docente\":{\"id\":5}}"
          )
        )
    )
    @ApiResponse(responseCode = "200", description = "Curso creado",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Curso.class)))
    // —————————————————————————————————————————————————————————————
    @PostMapping
    public Curso crear(@RequestBody Curso curso) {
        return cursoService.guardar(curso);
    }

    // PUT /api/cursos/{id}
    // Actualiza un curso existente identificado por id con los datos del cuerpo JSON
    // —————————————————————————————————————————————————————————————
    // —[OpenAPI]— Actualizar curso existente
    @Operation(summary = "Actualizar curso",
               description = "Modifica un curso existente identificado por ID")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos actualizados del curso",
        required = true,
        content = @Content(mediaType = "application/json",
                           schema  = @Schema(implementation = Curso.class))
    )
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Curso actualizado",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Curso.class))),
      @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    // —————————————————————————————————————————————————————————————
    @PutMapping("/{id}")
    public ResponseEntity<Curso> actualizar(@PathVariable Long id, @RequestBody Curso curso) {
        return cursoService.actualizar(id, curso)
                .map(ResponseEntity::ok) // si actualizó, devuelve 200 con el curso actualizado
                .orElse(ResponseEntity.notFound().build()); // si no encontró el curso, 404
    }

    // DELETE /api/cursos/{id}
    // Elimina un curso por id, responde 204 (sin contenido) si se eliminó, o 404 si no existe
    // —————————————————————————————————————————————————————————————
    // —[OpenAPI]— Eliminar curso
    @Operation(summary = "Eliminar curso",
               description = "Borra un curso existente; devuelve 204 o 404")
    @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Curso eliminado"),
      @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    // —————————————————————————————————————————————————————————————
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (cursoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/cursos/docente/{docenteId}
    // Lista los cursos asociados a un docente específico
    // —————————————————————————————————————————————————————————————
    // —[OpenAPI]— Listar cursos por docente
    @Operation(summary = "Listar cursos de un docente",
               description = "Devuelve cursos asociados a un docente válido o 403 si no lo es")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Cursos del docente",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Curso.class, type = "array"))),
      @ApiResponse(responseCode = "403", description = "Usuario no autorizado")
    })
    // —————————————————————————————————————————————————————————————
    @GetMapping("/docente/{docenteId}")
    public ResponseEntity<List<Curso>> listarPorDocente(@PathVariable Long docenteId) {
        // Busca al docente por id
        Optional<Estudiante> docenteOpt = estudianteService.buscarPorId(docenteId);

        // Si no existe o su rol no es "DOCENTE", responde 403 Forbidden
        if (docenteOpt.isEmpty() || !"DOCENTE".equals(docenteOpt.get().getRol())) {
            return ResponseEntity.status(403).build();
        }

        // Obtiene la lista de cursos asociados a ese docente
        List<Curso> cursos = cursoService.listarPorDocente(docenteId);
        return ResponseEntity.ok(cursos);
    }

    // POST /api/cursos/docente/{docenteId}
    // Crea un curso nuevo asignado a un docente específico
    // —————————————————————————————————————————————————————————————
    // —[OpenAPI]— Crear curso para docente
    @Operation(summary = "Crear curso para docente",
               description = "Asocia y guarda un nuevo curso a un docente válido")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del curso y ID de docente",
        required = true,
        content = @Content(mediaType = "application/json",
                           schema  = @Schema(implementation = Curso.class))
    )
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Curso creado para docente",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Curso.class))),
      @ApiResponse(responseCode = "403", description = "Usuario no autorizado")
    })
    // —————————————————————————————————————————————————————————————
    @PostMapping("/docente/{docenteId}")
    public ResponseEntity<?> crearCursoDocente(@PathVariable Long docenteId, @RequestBody Curso curso) {
        Optional<Estudiante> docenteOpt = estudianteService.buscarPorId(docenteId);

        // Verifica que el usuario exista y sea docente
        if (docenteOpt.isEmpty() || !"DOCENTE".equals(docenteOpt.get().getRol())) {
            return ResponseEntity.status(403).body("Solo docentes pueden crear cursos");
        }

        // Asocia el curso al docente encontrado
        curso.setDocente(docenteOpt.get());

        // Guarda el curso y lo devuelve en la respuesta
        Curso nuevoCurso = cursoService.guardar(curso);
        return ResponseEntity.ok(nuevoCurso);
    }


    // —————————————————————————————————————————————————————————————
    // BLOQUE EXTRA HATEOAS: Rutas adicionales para cumplir HATEOAS
    // —————————————————————————————————————————————————————————————

    /**
     * —[HATEOAS]— Listar todos los cursos con enlaces
     * Cada Curso lleva un enlace “self” y la colección un enlace “self”
     */
    @GetMapping("/hateoas")
    public ResponseEntity<CollectionModel<EntityModel<Curso>>> listarHateoas() {
        var modelos = cursoService.listarTodos().stream()
            .map(c -> {
                EntityModel<Curso> model = EntityModel.of(c);
                model.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(CursoController.class)
                        .buscarHateoas(c.getId())
                ).withSelfRel());
                return model;
            }).toList();
        CollectionModel<EntityModel<Curso>> coleccion = CollectionModel.of(modelos);
        coleccion.add(WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(CursoController.class)
                .listarHateoas()
        ).withSelfRel());
        return ResponseEntity.ok(coleccion);
    }

    /**
     * —[HATEOAS]— Buscar curso por ID con enlaces
     * Añade enlace “self” y enlace “todos-los-cursos”
     */
    @GetMapping("/{id}/hateoas")
    public ResponseEntity<EntityModel<Curso>> buscarHateoas(@PathVariable Long id) {
        return cursoService.buscarPorId(id)
            .map(c -> {
                EntityModel<Curso> model = EntityModel.of(c);
                model.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(CursoController.class)
                        .buscarHateoas(id)
                ).withSelfRel());
                model.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(CursoController.class)
                        .listarHateoas()
                ).withRel("todos-los-cursos"));
                return ResponseEntity.ok(model);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    // —————————————————————————————————————————————————————————————

}


