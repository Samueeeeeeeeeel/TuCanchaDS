package com.example.Canchas.controller;

import com.example.Canchas.model.Cancha;
import com.example.Canchas.service.CanchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canchas")
@RequiredArgsConstructor
@Tag(name = "Canchas", description = "API para la gestión de canchas deportivas")
public class CanchaController {
    
    private final CanchaService canchaService;
    
    @Operation(summary = "Obtener todas las canchas", description = "Retorna una lista con todas las canchas registradas en el sistema, incluyendo activas e inactivas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de canchas obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class)))
    })
    @GetMapping
    public ResponseEntity<List<Cancha>> obtenerTodasLasCanchas() {
        return ResponseEntity.ok(canchaService.obtenerTodasLasCanchas());
    }
    
    @Operation(summary = "Obtener canchas activas", description = "Retorna una lista con todas las canchas que están actualmente activas y disponibles para reservas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de canchas activas obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class)))
    })
    @GetMapping("/activas")
    public ResponseEntity<List<Cancha>> obtenerCanchasActivas() {
        return ResponseEntity.ok(canchaService.obtenerCanchasActivas());
    }
    
    @Operation(summary = "Obtener cancha por ID", description = "Retorna los detalles de una cancha específica identificada por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancha encontrada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class))),
        @ApiResponse(responseCode = "404", description = "Cancha no encontrada - El ID proporcionado no existe en el sistema",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Cancha> obtenerCanchaPorId(
            @Parameter(description = "ID de la cancha a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return canchaService.obtenerCanchaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Buscar canchas por tipo", description = "Retorna todas las canchas de un tipo específico (Fútbol, Tenis, Básquet, etc.)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de canchas del tipo especificado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class)))
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Cancha>> obtenerCanchasPorTipo(
            @Parameter(description = "Tipo de cancha a buscar", required = true, example = "Fútbol")
            @PathVariable String tipo) {
        return ResponseEntity.ok(canchaService.obtenerCanchasPorTipo(tipo));
    }
    
    @Operation(summary = "Buscar canchas activas por ciudad", description = "Retorna todas las canchas activas ubicadas en una ciudad específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de canchas activas en la ciudad especificada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class)))
    })
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Cancha>> obtenerCanchasPorCiudad(
            @Parameter(description = "Ciudad donde buscar canchas", required = true, example = "Bogotá")
            @PathVariable String ciudad) {
        return ResponseEntity.ok(canchaService.obtenerCanchasActivasPorCiudad(ciudad));
    }
    
    @Operation(summary = "Crear nueva cancha", description = "Crea una nueva cancha deportiva en el sistema. La cancha se crea activa por defecto.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cancha creada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class))),
        @ApiResponse(responseCode = "400", description = "Error de validación - Los datos proporcionados no son válidos",
                content = @Content)
    })
    @PostMapping
    public ResponseEntity<Cancha> crearCancha(
            @Parameter(description = "Datos de la cancha a crear", required = true)
            @RequestBody Cancha cancha) {
        Cancha nuevaCancha = canchaService.crearCancha(cancha);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCancha);
    }
    
    @Operation(summary = "Actualizar cancha", description = "Actualiza los datos de una cancha existente. Si la cancha no existe, retorna 404.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancha actualizada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cancha.class))),
        @ApiResponse(responseCode = "404", description = "Cancha no encontrada - El ID proporcionado no existe en el sistema",
                content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Cancha> actualizarCancha(
            @Parameter(description = "ID de la cancha a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la cancha", required = true)
            @RequestBody Cancha cancha) {
        try {
            Cancha canchaActualizada = canchaService.actualizarCancha(id, cancha);
            return ResponseEntity.ok(canchaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Eliminar cancha", description = "Elimina permanentemente una cancha del sistema. Esta acción no se puede deshacer.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cancha eliminada exitosamente",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCancha(
            @Parameter(description = "ID de la cancha a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        canchaService.eliminarCancha(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Desactivar cancha", description = "Desactiva una cancha sin eliminarla. Una cancha desactivada no aparecerá en búsquedas de canchas activas y no podrá recibir nuevas reservas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cancha desactivada exitosamente",
                content = @Content)
    })
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarCancha(
            @Parameter(description = "ID de la cancha a desactivar", required = true, example = "1")
            @PathVariable Long id) {
        canchaService.desactivarCancha(id);
        return ResponseEntity.ok().build();
    }
}