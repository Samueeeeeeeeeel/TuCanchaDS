package com.tucancha.data.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Reserva para el microservicio Reservas (Puerto 8084)
 */
data class Reserva(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("usuarioId")
    val usuarioId: Long,
    
    @SerializedName("canchaId")
    val canchaId: Long,
    
    @SerializedName("fechaInicio")
    val fechaInicio: String,  // Formato ISO 8601: "2024-01-15T10:00:00"
    
    @SerializedName("fechaFin")
    val fechaFin: String,
    
    @SerializedName("precioTotal")
    val precioTotal: Double,
    
    @SerializedName("estado")
    val estado: EstadoReserva? = EstadoReserva.PENDIENTE,
    
    @SerializedName("observaciones")
    val observaciones: String? = null,
    
    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null,
    
    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String? = null
)

enum class EstadoReserva {
    PENDIENTE,
    CONFIRMADA,
    CANCELADA,
    COMPLETADA
}

/**
 * Request para crear reserva (campos mínimos)
 */
data class CrearReservaRequest(
    val usuarioId: Long,
    val canchaId: Long,
    val fechaInicio: String,
    val fechaFin: String,
    val precioTotal: Double,
    val observaciones: String? = null
)

