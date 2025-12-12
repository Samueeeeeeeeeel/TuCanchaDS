package com.example.Reservas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa una reserva de cancha realizada por un usuario")
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la reserva", example = "1")
    private Long id;
    
    @Column(name = "usuario_id", nullable = false)
    @Schema(description = "ID del usuario que realiza la reserva", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long usuarioId;
    
    @Column(name = "cancha_id", nullable = false)
    @Schema(description = "ID de la cancha reservada", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long canchaId;
    
    @Column(name = "fecha_inicio", nullable = false)
    @Schema(description = "Fecha y hora de inicio de la reserva", example = "2024-01-15T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    @Schema(description = "Fecha y hora de fin de la reserva", example = "2024-01-15T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaFin;
    
    @Column(nullable = false)
    @Schema(description = "Precio total de la reserva", example = "100000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal precioTotal;
    
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Estado actual de la reserva", example = "PENDIENTE", allowableValues = {"PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA"})
    private EstadoReserva estado;
    
    @Column(length = 500)
    @Schema(description = "Observaciones adicionales sobre la reserva", example = "Reserva para partido de fútbol")
    private String observaciones;
    
    @Column(name = "fecha_creacion")
    @Schema(description = "Fecha y hora de creación de la reserva", example = "2024-01-15T09:00:00")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    @Schema(description = "Fecha y hora de la última actualización", example = "2024-01-15T09:00:00")
    private LocalDateTime fechaActualizacion;
    
    @Schema(description = "Estados posibles de una reserva")
    public enum EstadoReserva {
        @Schema(description = "Reserva creada pero aún no confirmada")
        PENDIENTE,
        @Schema(description = "Reserva confirmada y activa")
        CONFIRMADA,
        @Schema(description = "Reserva cancelada")
        CANCELADA,
        @Schema(description = "Reserva completada (ya se utilizó)")
        COMPLETADA
    }
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoReserva.PENDIENTE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}