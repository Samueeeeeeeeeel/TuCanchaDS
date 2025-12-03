package com.example.Disponibilidad.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un período de disponibilidad o no disponibilidad de una cancha")
public class Disponibilidad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la disponibilidad", example = "1")
    private Long id;
    
    @Column(name = "cancha_id", nullable = false)
    @Schema(description = "ID de la cancha a la que pertenece esta disponibilidad", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long canchaId;
    
    @Column(name = "fecha_inicio", nullable = false)
    @Schema(description = "Fecha y hora de inicio del período de disponibilidad", example = "2024-01-15T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    @Schema(description = "Fecha y hora de fin del período de disponibilidad", example = "2024-01-15T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaFin;
    
    @Column(nullable = false)
    @Schema(description = "Indica si la cancha está disponible en este período", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean disponible;
    
    @Column(length = 200)
    @Schema(description = "Motivo por el cual la cancha no está disponible (si aplica)", example = "Mantenimiento programado")
    private String motivoNoDisponible;
    
    @Column(name = "fecha_creacion")
    @Schema(description = "Fecha y hora de creación del registro", example = "2024-01-15T09:00:00")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    @Schema(description = "Fecha y hora de la última actualización", example = "2024-01-15T09:00:00")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (disponible == null) {
            disponible = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}