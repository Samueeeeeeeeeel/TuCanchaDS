package com.tucancha.data.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Usuario para el microservicio Login (Puerto 8083)
 */
data class Usuario(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("apellido")
    val apellido: String? = null,
    
    @SerializedName("telefono")
    val telefono: String? = null,
    
    @SerializedName("activo")
    val activo: Boolean? = true,
    
    @SerializedName("rol")
    val rol: Rol? = Rol.USUARIO,
    
    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null,
    
    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String? = null,
    
    @SerializedName("ultimoAcceso")
    val ultimoAcceso: String? = null
)

enum class Rol {
    ADMIN,
    USUARIO
}

/**
 * Request para login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Response del login exitoso
 */
data class LoginResponse(
    val mensaje: String,
    val usuario: Usuario
)

/**
 * Response de error genérico
 */
data class ErrorResponse(
    val error: String
)

