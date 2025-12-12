package com.example.Login.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa un usuario del sistema con información de autenticación y roles")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1")
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "Email único del usuario, utilizado para login", example = "usuario@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @Column(nullable = false, length = 255)
    @Schema(description = "Contraseña del usuario (encriptada en producción)", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    
    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del usuario", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @Column(length = 100)
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String apellido;
    
    @Column(length = 20)
    @Schema(description = "Número de teléfono del usuario", example = "3001234567")
    private String telefono;
    
    @Column(nullable = false)
    @Schema(description = "Indica si el usuario está activo en el sistema", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean activo;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Rol del usuario en el sistema", example = "USUARIO", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"ADMIN", "USUARIO"})
    private Rol rol;
    
    @Column(name = "fecha_creacion")
    @Schema(description = "Fecha y hora de creación del registro", example = "2024-01-15T10:00:00")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    @Schema(description = "Fecha y hora de la última actualización", example = "2024-01-15T10:00:00")
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "ultimo_acceso")
    @Schema(description = "Fecha y hora del último acceso al sistema", example = "2024-01-15T10:00:00")
    private LocalDateTime ultimoAcceso;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
        if (rol == null) {
            rol = Rol.USUARIO; // Por defecto es usuario general
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}