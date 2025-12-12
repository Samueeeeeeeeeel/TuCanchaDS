package com.example.Disponibilidad.controller;

import com.example.Disponibilidad.model.Disponibilidad;
import com.example.Disponibilidad.service.DisponibilidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidades")
@RequiredArgsConstructor
@Tag(name = "Disponibilidad", description = "API para la gestión de disponibilidad de canchas")
public class DisponibilidadController {
    
    private final DisponibilidadService disponibilidadService;
    
    @Operation(summary = "Obtener todas las disponibilidades", description = "Retorna una lista con todas las disponibilidades registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de disponibilidades obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disponibilidad.class)))
    })
    @GetMapping
    public ResponseEntity<List<Disponibilidad>> obtenerTodasLasDisponibilidades() {
        return ResponseEntity.ok(disponibilidadService.obtenerTodasLasDisponibilidades());
    }
    
    @Operation(summary = "Obtener disponibilidad por ID", description = "Retorna los detalles de una disponibilidad específica identificada por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad encontrada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disponibilidad.class))),
        @ApiResponse(responseCode = "404", description = "Disponibilidad no encontrada - El ID proporcionado no existe en el sistema",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Disponibilidad> obtenerDisponibilidadPorId(
            @Parameter(description = "ID de la disponibilidad a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return disponibilidadService.obtenerDisponibilidadPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Obtener disponibilidades activas por cancha", description = "Retorna todas las disponibilidades activas (disponibles) de una cancha específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de disponibilidades activas de la cancha",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disponibilidad.class)))
    })
    @GetMapping("/cancha/{canchaId}")
    public ResponseEntity<List<Disponibilidad>> obtenerDisponibilidadesPorCancha(
            @Parameter(description = "ID de la cancha", required = true, example = "1")
            @PathVariable Long canchaId) {
        return ResponseEntity.ok(disponibilidadService.obtenerDisponibilidadesActivasPorCancha(canchaId));
    }
    
    @Operation(summary = "Obtener disponibilidades por cancha y rango de fechas", description = "Retorna las disponibilidades de una cancha dentro de un rango de fechas específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de disponibilidades en el rango especificado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disponibilidad.class)))
    })
    @GetMapping("/cancha/{canchaId}/rango")
    public ResponseEntity<List<Disponibilidad>> obtenerDisponibilidadesPorRango(
            @Parameter(description = "ID de la cancha", required = true, example = "1")
            @PathVariable Long canchaId,
            @Parameter(description = "Fecha y hora de inicio del rango (ISO 8601)", required = true, example = "2024-01-15T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha y hora de fin del rango (ISO 8601)", required = true, example = "2024-01-15T12:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(disponibilidadService.obtenerDisponibilidadesPorRango(
                canchaId, fechaInicio, fechaFin));
    }
    
    @Operation(summary = "Obtener disponibilidades disponibles por rango", description = "Retorna todas las disponibilidades disponibles (activas) dentro de un rango de fechas, sin filtrar por cancha")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de disponibilidades disponibles en el rango",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disponibilidad.class)))
    })
    @GetMapping("/rango")
    public ResponseEntity<List<Disponibilidad>> obtenerDisponibilidadesDisponiblesPorRango(
            @Parameter(description = "Fecha y hora de inicio del rango (ISO 8601)", required = true, example = "2024-01-15T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha y hora de fin del rango (ISO 8601)", required = true, example = "2024-01-15T12:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(disponibilidadService.obtenerDisponibilidadesDisponiblesPorRango(
                fechaInicio, fechaFin));
    }
    
    @Operation(summary = "Verificar disponibilidad", description = "Verifica si una cancha está disponible en un rango de fechas específico. Retorna true si está disponible, false si hay conflictos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificación completada",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Boolean.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "true")))
    })
    @GetMapping("/verificar")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @Parameter(description = "ID de la cancha a verificar", required = true, example = "1")
            @RequestParam Long canchaId,
            @Parameter(description = "Fecha y hora de inicio (ISO 8601)", required = true, example = "2024-01-15T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha y hora de fin (ISO 8601)", required = true, example = "2024-01-15T12:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        boolean disponible = disponibilidadService.verificarDisponibilidad(canchaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(disponible);
    }
    
    @Operation(summary = "Crear disponibilidad", description = "Crea un nuevo período de disponibilidad para una cancha. Por defecto se crea como disponible.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Disponibilidad creada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disponibilidad.class)))
    })
    @PostMapping
    public ResponseEntity<Disponibilidad> crearDisponibilidad(
            @Parameter(description = "Datos de la disponibilidad a crear", required = true)
            @RequestBody Disponibilidad disponibilidad) {
        Disponibilidad nuevaDisponibilidad = disponibilidadService.crearDisponibilidad(disponibilidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaDisponibilidad);
    }
    
    @Operation(summary = "Actualizar disponibilidad", description = "Actualiza los datos de una disponibilidad existente. Si la disponibilidad no existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Disponibilidad.class))),
        @ApiResponse(responseCode = "404", description = "Disponibilidad no encontrada - El ID proporcionado no existe en el sistema",
                content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Disponibilidad> actualizarDisponibilidad(
            @Parameter(description = "ID de la disponibilidad a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la disponibilidad", required = true)
            @RequestBody Disponibilidad disponibilidad) {
        try {
            Disponibilidad disponibilidadActualizada = disponibilidadService.actualizarDisponibilidad(id, disponibilidad);
            return ResponseEntity.ok(disponibilidadActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Eliminar disponibilidad", description = "Elimina permanentemente una disponibilidad del sistema. Esta acción no se puede deshacer.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Disponibilidad eliminada exitosamente",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDisponibilidad(
            @Parameter(description = "ID de la disponibilidad a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        disponibilidadService.eliminarDisponibilidad(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Marcar como no disponible", description = "Marca una disponibilidad como no disponible, útil para períodos de mantenimiento o eventos especiales. Se puede incluir un motivo opcional.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidad marcada como no disponible exitosamente",
                content = @Content)
    })
    @PatchMapping("/{id}/no-disponible")
    public ResponseEntity<Void> marcarComoNoDisponible(
            @Parameter(description = "ID de la disponibilidad a marcar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Motivo por el cual no está disponible (opcional)", example = "Mantenimiento programado")
            @RequestParam(required = false) String motivo) {
        disponibilidadService.marcarComoNoDisponible(id, motivo);
        return ResponseEntity.ok().build();
    }
}