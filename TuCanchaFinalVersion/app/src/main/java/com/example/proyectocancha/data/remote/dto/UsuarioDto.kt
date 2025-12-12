package com.example.proyectocancha.data.remote.dto

data class UsuarioDto(
    val id: Long? = null,
    val nombre: String,
    val apellido: String? = null,
    val email: String,
    val telefono: String? = null,
    val password: String? = null, // Solo para registro, no se envía en otras operaciones
    val rol: String? = null,
    val activo: Boolean? = null
)

