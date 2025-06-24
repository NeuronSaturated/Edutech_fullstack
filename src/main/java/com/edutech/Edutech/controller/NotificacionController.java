package com.edutech.Edutech.controller;

import java.net.URI;
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

import com.edutech.Edutech.model.Notificacion;
import com.edutech.Edutech.service.NotificacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
// —————————————————————————————————————————————————————————————

// Controlador REST para manejar solicitudes relacionadas con notificaciones
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    // Inyección del servicio que maneja la lógica y datos de notificaciones
    @Autowired
    private NotificacionService service;

    // GET /api/notificaciones
    // Devuelve la lista completa de todas las notificaciones
    // ————————————————————————————————————————————
    @Operation(summary = "Listar todas las notificaciones",
               description = "Devuelve todas las notificaciones registradas")
    @ApiResponse(responseCode = "200",
                 description = "Listado de notificaciones",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Notificacion.class, type = "array")))
    @GetMapping
    public List<Notificacion> listar() {
        return service.listarTodos();
    }
    // ————————————————————————————————————————————

    // GET /api/notificaciones/{id}
    // Busca una notificación por su id
    // Responde 200 con la notificación si existe o 404 si no la encuentra
    // ————————————————————————————————————————————
    @Operation(summary = "Buscar notificación por ID",
               description = "Devuelve la notificación si existe, o 404 si no la encuentra")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Notificación encontrada",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Notificacion.class))),
      @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // ————————————————————————————————————————————

    // POST /api/notificaciones
    // Crea una nueva notificación con los datos recibidos en el cuerpo de la solicitud
    // ————————————————————————————————————————————
    @Operation(summary = "Crear notificación",
               description = "Registra una nueva notificación con los datos proporcionados")
    @ApiResponse(responseCode = "201",
                 description = "Notificación creada",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Notificacion.class)))
    @PostMapping
    public ResponseEntity<Notificacion> crear(@RequestBody Notificacion notificacion) {
        Notificacion nueva = service.guardar(notificacion);
        URI location = URI.create("/api/notificaciones/" + nueva.getId());
        return ResponseEntity.created(location).body(nueva);
    }
    // ————————————————————————————————————————————

    // PUT /api/notificaciones/{id}
    // Actualiza una notificación existente identificada por id con los nuevos datos
    // Responde 200 con la notificación actualizada o 404 si no se encuentra
    // ————————————————————————————————————————————
    @Operation(summary = "Actualizar notificación",
               description = "Modifica una notificación existente por ID")
    @ApiResponses({
      @ApiResponse(responseCode = "200",
                   description = "Notificación actualizada",
                   content = @Content(mediaType = "application/json",
                                      schema = @Schema(implementation = Notificacion.class))),
      @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable Long id,
                                                   @RequestBody Notificacion notificacion) {
        return service.actualizar(id, notificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // ————————————————————————————————————————————

    // DELETE /api/notificaciones/{id}
    // Elimina una notificación por id
    // Responde 204 No Content si fue eliminada o 404 si no existe
    // ————————————————————————————————————————————
    @Operation(summary = "Eliminar notificación",
               description = "Borra una notificación existente por ID")
    @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Notificación eliminada"),
      @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    // ————————————————————————————————————————————

    // —————————————————————————————————————————————————————————————
    // BLOQUE EXTRA HATEOAS: rutas adicionales para cumplir HATEOAS
    // —————————————————————————————————————————————————————————————

    /**
     * —[HATEOAS]— Listar todas las notificaciones con enlaces
     * Cada Notificacion lleva un enlace “self” y la colección un enlace “self”
     */
    @GetMapping("/hateoas")
    public ResponseEntity<CollectionModel<EntityModel<Notificacion>>> listarHateoas() {
        var modelos = service.listarTodos().stream()
            .map(n -> {
                EntityModel<Notificacion> m = EntityModel.of(n);
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(NotificacionController.class)
                        .buscar(n.getId())
                ).withSelfRel());
                return m;
            })
            .toList();
        CollectionModel<EntityModel<Notificacion>> coleccion = CollectionModel.of(modelos);
        coleccion.add(WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(NotificacionController.class)
                .listarHateoas()
        ).withSelfRel());
        return ResponseEntity.ok(coleccion);
    }

    /**
     * —[HATEOAS]— Buscar notificación por ID con enlaces
     * Añade enlace “self” y enlace “todas-las-notificaciones”
     */
    @GetMapping("/{id}/hateoas")
    public ResponseEntity<EntityModel<Notificacion>> buscarHateoas(@PathVariable Long id) {
        return service.buscarPorId(id)
            .map(n -> {
                EntityModel<Notificacion> m = EntityModel.of(n);
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(NotificacionController.class)
                        .buscarHateoas(id)
                ).withSelfRel());
                m.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(NotificacionController.class)
                        .listarHateoas()
                ).withRel("todas-las-notificaciones"));
                return ResponseEntity.ok(m);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    // —————————————————————————————————————————————————————————————

}
