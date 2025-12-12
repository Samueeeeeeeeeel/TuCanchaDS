package com.example.proyectocancha.data.remote.dto

data class ReservaDto(
    val id: Long? = null,
    val usuarioId: Long? = null,
    val canchaId: Long,
    val fechaInicio: String, // Formato ISO 8601: "2024-01-15T10:00:00"
    val fechaFin: String? = null,
    val precioTotal: Double,
    val estado: EstadoReserva? = EstadoReserva.PENDIENTE,
    val observaciones: String? = null,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
)

