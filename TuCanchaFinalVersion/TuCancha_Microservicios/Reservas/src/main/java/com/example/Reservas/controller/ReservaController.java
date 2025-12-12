package com.example.Reservas.controller;

import com.example.Reservas.model.Reserva;
import com.example.Reservas.service.ReservaService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API para la gestión de reservas de canchas")
public class ReservaController {
    
    private final ReservaService reservaService;
    
    @Operation(summary = "Obtener todas las reservas", description = "Retorna una lista con todas las reservas registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class)))
    })
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodasLasReservas() {
        return ResponseEntity.ok(reservaService.obtenerTodasLasReservas());
    }
    
    @Operation(summary = "Obtener reserva por ID", description = "Retorna los detalles de una reserva específica identificada por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva encontrada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada - El ID proporcionado no existe en el sistema",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(
            @Parameter(description = "ID de la reserva a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return reservaService.obtenerReservaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Obtener reservas por usuario", description = "Retorna todas las reservas realizadas por un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas del usuario",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class)))
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorUsuario(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorUsuario(usuarioId));
    }
    
    @Operation(summary = "Obtener reservas por cancha", description = "Retorna todas las reservas de una cancha específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas de la cancha",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class)))
    })
    @GetMapping("/cancha/{canchaId}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorCancha(
            @Parameter(description = "ID de la cancha", required = true, example = "1")
            @PathVariable Long canchaId) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorCancha(canchaId));
    }
    
    @Operation(summary = "Obtener reservas por estado", description = "Retorna todas las reservas con un estado específico (PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas con el estado especificado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class)))
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorEstado(
            @Parameter(description = "Estado de la reserva", required = true, example = "PENDIENTE",
                    schema = @Schema(allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA"}))
            @PathVariable Reserva.EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorEstado(estado));
    }
    
    @Operation(summary = "Obtener reservas por usuario y estado", description = "Retorna las reservas de un usuario específico con un estado determinado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas del usuario con el estado especificado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class)))
    })
    @GetMapping("/usuario/{usuarioId}/estado/{estado}")
    public ResponseEntity<List<Reserva>> obtenerReservasPorUsuarioYEstado(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long usuarioId,
            @Parameter(description = "Estado de la reserva", required = true, example = "CONFIRMADA")
            @PathVariable Reserva.EstadoReserva estado) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorUsuarioYEstado(usuarioId, estado));
    }
    
    @Operation(summary = "Obtener reservas por rango de fechas", description = "Retorna todas las reservas dentro de un rango de fechas específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reservas en el rango especificado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class)))
    })
    @GetMapping("/rango")
    public ResponseEntity<List<Reserva>> obtenerReservasPorRango(
            @Parameter(description = "Fecha y hora de inicio del rango (ISO 8601)", required = true, example = "2024-01-15T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha y hora de fin del rango (ISO 8601)", required = true, example = "2024-01-15T12:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorRango(fechaInicio, fechaFin));
    }
    
    @Operation(summary = "Verificar disponibilidad para reserva", description = "Verifica si una cancha está disponible para reservar en un rango de fechas. Retorna true si no hay conflictos con otras reservas.")
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
        boolean disponible = reservaService.verificarDisponibilidad(canchaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(disponible);
    }
    
    @Operation(summary = "Crear nueva reserva", description = "Crea una nueva reserva de cancha. Valida que no haya conflictos de horario antes de crear. Si la cancha no está disponible, retorna error 400.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación - La cancha no está disponible en el horario seleccionado o hay conflictos con otras reservas",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"La cancha no está disponible en el horario seleccionado\"}")))
    })
    @PostMapping
    public ResponseEntity<?> crearReserva(
            @Parameter(description = "Datos de la reserva a crear", required = true)
            @RequestBody Reserva reserva) {
        try {
            Reserva nuevaReserva = reservaService.crearReserva(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @Operation(summary = "Actualizar reserva", description = "Actualiza los datos de una reserva existente. Si se cambia el horario o cancha, valida disponibilidad. Si la reserva no existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación - La cancha no está disponible en el nuevo horario o hay conflictos",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"La cancha no está disponible en el horario seleccionado\"}"))),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada - El ID proporcionado no existe",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Reserva no encontrada con id: 999\"}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReserva(
            @Parameter(description = "ID de la reserva a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la reserva", required = true)
            @RequestBody Reserva reserva) {
        try {
            Reserva reservaActualizada = reservaService.actualizarReserva(id, reserva);
            return ResponseEntity.ok(reservaActualizada);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @Operation(summary = "Eliminar reserva", description = "Elimina permanentemente una reserva del sistema. Esta acción no se puede deshacer.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(
            @Parameter(description = "ID de la reserva a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Confirmar reserva", description = "Cambia el estado de una reserva de PENDIENTE a CONFIRMADA. Si la reserva no existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva confirmada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada - El ID proporcionado no existe",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Reserva no encontrada con id: 999\"}")))
    })
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(
            @Parameter(description = "ID de la reserva a confirmar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Reserva reserva = reservaService.confirmarReserva(id);
            return ResponseEntity.ok(reserva);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @Operation(summary = "Cancelar reserva", description = "Cancela una reserva cambiando su estado a CANCELADA. Se puede incluir un motivo opcional. Si la reserva no existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada - El ID proporcionado no existe",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Reserva no encontrada con id: 999\"}")))
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(
            @Parameter(description = "ID de la reserva a cancelar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Motivo de la cancelación (opcional)", example = "Cambio de planes")
            @RequestParam(required = false) String motivo) {
        try {
            Reserva reserva = reservaService.cancelarReserva(id, motivo);
            return ResponseEntity.ok(reserva);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @Operation(summary = "Completar reserva", description = "Marca una reserva como completada, indicando que ya se utilizó. Cambia el estado a COMPLETADA. Si la reserva no existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva completada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada - El ID proporcionado no existe",
                content = @Content(schema = @Schema(implementation = Map.class),
                        examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                value = "{\"error\": \"Reserva no encontrada con id: 999\"}")))
    })
    @PatchMapping("/{id}/completar")
    public ResponseEntity<?> completarReserva(
            @Parameter(description = "ID de la reserva a completar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Reserva reserva = reservaService.completarReserva(id);
            return ResponseEntity.ok(reserva);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}