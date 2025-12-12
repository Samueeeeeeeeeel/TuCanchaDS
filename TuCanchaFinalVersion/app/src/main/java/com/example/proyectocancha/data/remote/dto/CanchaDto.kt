package com.example.proyectocancha.data.remote.dto

data class CanchaDto(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String? = null,
    val tipo: String,
    val precioPorHora: Int,
    val direccion: String,
    val ciudad: String? = null,
    val imagenUrl: String? = null,
    val activa: Boolean = true
)

