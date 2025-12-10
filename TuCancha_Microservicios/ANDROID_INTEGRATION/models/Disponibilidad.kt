package com.tucancha.data.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de Disponibilidad para el microservicio Disponibilidad (Puerto 8082)
 */
data class Disponibilidad(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("canchaId")
    val canchaId: Long,
    
    @SerializedName("fechaInicio")
    val fechaInicio: String,  // Formato ISO 8601: "2024-01-15T10:00:00"
    
    @SerializedName("fechaFin")
    val fechaFin: String,
    
    @SerializedName("disponible")
    val disponible: Boolean? = true,
    
    @SerializedName("motivoNoDisponible")
    val motivoNoDisponible: String? = null,
    
    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null,
    
    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String? = null
)

