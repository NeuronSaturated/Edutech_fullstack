package com.edutech.Edutech.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.Edutech.model.Estudiante;
import com.edutech.Edutech.service.EstudianteService;
import com.edutech.Edutech.service.InscripcionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// —————————————————————————————————————————————————————————————

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
public class EstudianteController {

    // Inyección del servicio para manejar la lógica y datos de estudiantes
    @Autowired
    private EstudianteService service;

    // GET /api/estudiantes
    // Devuelve una lista con todos los estudiantes
    @GetMapping
    // ————————————————————————————————————————————
    @Operation(summary = "Listar todos los estudiantes",
               description = "Obtiene la lista completa de estudiantes registrados")
    @ApiResponse(responseCode = "200",
                 description = "Listado de estudiantes",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Estudiante.class, type = "array")))
    // ————————————————————————————————————————————
    public List<Estudiante> listar() {
        return service.listarTodos();
    }

    // GET /api/estudiantes/{id}
    // Busca un estudiante por su id y devuelve 200 con el estudiante o 404 si no existe
    // ————————————————————————————————————————————
    @GetMapping("/{id}")
    @Operation(summary = "Buscar estudiante por ID",
               description = "Devuelve un estudiante si existe, o 404 si no se encuentra")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                     description = "Estudiante encontrado",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Estudiante.class))),
        @ApiResponse(responseCode = "404", description = "No se encontró el estudiante")
    })
    // ————————————————————————————————————————————
    public ResponseEntity<EntityModel<Estudiante>> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
            .map(est -> {
                // Enlaces HATEOAS relativos
                EntityModel<Estudiante> model = EntityModel.of(est,
                    Link.of("/api/estudiantes/" + id).withSelfRel(),
                    Link.of("/api/estudiantes").withRel("estudiantes")
                );
                return ResponseEntity.ok(model);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/estudiantes
    // Crea un nuevo estudiante con los datos recibidos en el cuerpo JSON
    // ————————————————————————————————————————————
    @PostMapping
    @Operation(summary = "Crear estudiante",
               description = "Registra un nuevo estudiante")
    @ApiResponse(responseCode = "201",
                 description = "Estudiante creado",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Estudiante.class)))
    // ————————————————————————————————————————————
    public ResponseEntity<Estudiante> crear(@RequestBody Estudiante estudiante) {
        Estudiante nuevo = service.guardar(estudiante);

        // ————————————————————————————————————————————
        // Construir Location RELATIVO: /api/estudiantes/{id}
        String path = "/api/estudiantes/" + nuevo.getId();
        // ————————————————————————————————————————————

        return ResponseEntity
                .created(URI.create(path)) // 201 + Location relativo
                .body(nuevo);
    }

    // PUT /api/estudiantes/{id}
    // Actualiza un estudiante existente identificado por id con datos del cuerpo JSON
    // ————————————————————————————————————————————
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estudiante",
               description = "Modifica un estudiante existente identificado por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                     description = "Estudiante actualizado",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Estudiante.class))),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    // ————————————————————————————————————————————
    public ResponseEntity<Estudiante> actualizar(@PathVariable Long id, @RequestBody Estudiante estudiante) {
        return service.actualizar(id, estudiante)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/estudiantes/{id}
    // Elimina un estudiante por id, devuelve 204 si fue eliminado, o 404 si no existe
    // ————————————————————————————————————————————
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar estudiante",
               description = "Borra un estudiante existente; devuelve 204 o 404")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Estudiante eliminado"),
        @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    // ————————————————————————————————————————————
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/estudiantes/register
    // Registro público para crear un nuevo usuario con rol fijo "ESTUDIANTE"
    // ————————————————————————————————————————————
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo estudiante",
               description = "Registra un estudiante con rol ESTUDIANTE")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro exitoso",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Estudiante.class))),
        @ApiResponse(responseCode = "400", description = "Error en datos de registro")
    })
    // ————————————————————————————————————————————
    public ResponseEntity<?> registrar(@RequestBody Estudiante estudiante) {
        try {
            estudiante.setRol("ESTUDIANTE"); // asegura que el rol siempre sea "ESTUDIANTE" al registrarse
            Estudiante nuevo = service.guardar(estudiante);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            // Si hay error (por ejemplo, email duplicado), devuelve un 400 con el mensaje
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Autowired
    private InscripcionService inscripcionService;

    // GET /api/estudiantes/cursos/{id}/estudiantes
    // Devuelve los estudiantes de un curso dado
    // ————————————————————————————————————————————
    @GetMapping("/cursos/{id}/estudiantes")
    @Operation(summary = "Listar estudiantes por curso",
               description = "Obtiene los estudiantes matriculados en un curso específico")
    @ApiResponse(responseCode = "200",
                 description = "Listado de estudiantes en el curso",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Estudiante.class, type = "array")))
    // ————————————————————————————————————————————
    public ResponseEntity<List<Estudiante>> getEstudiantesPorCurso(@PathVariable Long id) {
        List<Estudiante> estudiantes = inscripcionService.obtenerEstudiantesPorCursoId(id);
        return ResponseEntity.ok(estudiantes);
    }

    // POST /api/estudiantes/login
    // Autenticación por correo y contraseña
    // ————————————————————————————————————————————
    @PostMapping("/login")
    @Operation(summary = "Login de estudiante",
               description = "Autentica usuario por correo y password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Credenciales válidas",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Estudiante.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    // ————————————————————————————————————————————
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String correo = loginData.get("correo");
        String password = loginData.get("password");

        Optional<Estudiante> optEst = service.login(correo, password);
        if (optEst.isPresent()) {
            Estudiante est = optEst.get();
            // Devuelve 200 con el objeto estudiante si las credenciales son correctas
            return ResponseEntity.ok(est);
        } else {
            // Si no coincide, responde con 401 Unauthorized y mensaje de error
            return ResponseEntity.status(401).body("Correo o contraseña incorrectos");
        }
    }

    // POST /api/estudiantes/crear-docente?adminCorreo=...
    // Endpoint para crear un docente, solo accesible si el correo de quien hace la petición es de un admin
    // ————————————————————————————————————————————
    @PostMapping("/crear-docente")
    @Operation(summary = "Crear docente",
               description = "Registra un nuevo usuario con rol DOCENTE si es admin quien solicita")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Docente creado",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Estudiante.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    // ————————————————————————————————————————————
    public ResponseEntity<?> crearDocente(@RequestBody Estudiante docente, @RequestParam String adminCorreo) {
        Optional<Estudiante> adminOpt = service.buscarPorCorreo(adminCorreo);
        if (adminOpt.isEmpty() || !"ADMIN".equals(adminOpt.get().getRol())) {
            // Si no existe o no es admin, responde 403 Forbidden
            return ResponseEntity.status(403).body("No autorizado. Solo admins pueden crear docentes.");
        }
        try {
            docente.setRol("DOCENTE"); // fija rol de docente al nuevo usuario
            Estudiante nuevoDocente = service.guardar(docente);
            return ResponseEntity.ok(nuevoDocente);
        } catch (RuntimeException e) {
            // Captura errores como datos inválidos y responde con 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // —————————————————————————————————————————————————————————————
    // BLOQUE HATEOAS: rutas adicionales para cumplir HATEOAS
    // —————————————————————————————————————————————————————————————

    @GetMapping("/hateoas")
    public ResponseEntity<CollectionModel<EntityModel<Estudiante>>> listarHateoas() {
        var modelos = service.listarTodos().stream()
            .map(e -> {
                EntityModel<Estudiante> m = EntityModel.of(e);
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EstudianteController.class)
                        .buscarHateoas(e.getId())
                ).withSelfRel());
                return m;
            }).toList();
        CollectionModel<EntityModel<Estudiante>> coleccion = CollectionModel.of(modelos);
        coleccion.add(WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(EstudianteController.class)
                .listarHateoas()
        ).withSelfRel());
        return ResponseEntity.ok(coleccion);
    }

    @GetMapping("/{id}/hateoas")
    public ResponseEntity<EntityModel<Estudiante>> buscarHateoas(@PathVariable Long id) {
        return service.buscarPorId(id)
            .map(e -> {
                EntityModel<Estudiante> m = EntityModel.of(e);
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EstudianteController.class)
                        .buscarHateoas(id)
                ).withSelfRel());
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EstudianteController.class)
                        .listarHateoas()
                ).withRel("todos-los-estudiantes"));
                return ResponseEntity.ok(m);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    // —————————————————————————————————————————————————————————————

}
