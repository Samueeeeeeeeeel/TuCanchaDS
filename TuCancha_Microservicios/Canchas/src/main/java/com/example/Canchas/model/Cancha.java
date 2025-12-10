package com.example.Canchas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "canchas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa una cancha deportiva en el sistema")
public class Cancha {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la cancha", example = "1")
    private Long id;
    
    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre de la cancha", example = "Cancha de Fútbol 1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @Column(length = 200)
    @Schema(description = "Descripción detallada de la cancha", example = "Cancha de fútbol 11 con césped sintético")
    private String descripcion;
    
    @Column(nullable = false, length = 50)
    @Schema(description = "Tipo de cancha deportiva", example = "Fútbol", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"Fútbol", "Tenis", "Básquet", "Vóley"})
    private String tipo; // Fútbol, Tenis, Básquet, etc.
    
    @Column(nullable = false)
    @Schema(description = "Precio por hora de alquiler de la cancha (número entero)", example = "50000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer precioPorHora;
    
    @Column(nullable = false, length = 200)
    @Schema(description = "Dirección física de la cancha", example = "Calle 123 #45-67", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direccion;
    
    @Column(length = 50)
    @Schema(description = "Ciudad donde se encuentra la cancha", example = "Bogotá")
    private String ciudad;
    
    @Column(name = "imagen_url", length = 500)
    @Schema(description = "URL de la imagen/foto de la cancha", example = "https://example.com/cancha1.jpg")
    private String imagenUrl;
    
    @Column(nullable = false)
    @Schema(description = "Indica si la cancha está activa y disponible para reservas", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean activa;
    
    @Column(name = "fecha_creacion")
    @Schema(description = "Fecha y hora de creación del registro", example = "2024-01-15T10:00:00")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    @Schema(description = "Fecha y hora de la última actualización del registro", example = "2024-01-15T10:00:00")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}