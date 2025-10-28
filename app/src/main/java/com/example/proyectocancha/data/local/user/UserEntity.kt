package com.example.proyectocancha.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

// Entidad que representa la tabla "users"
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)] // email único
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,   // PK autogenerada
    val name: String,                                   // Nombre del usuario
    val email: String,                                  // Email único
    val phone: String,                                  // Teléfono (puede repetirse)
    val password: String,                               // Contraseña
    val isAdmin: Boolean = false                        // Rol administrador
)