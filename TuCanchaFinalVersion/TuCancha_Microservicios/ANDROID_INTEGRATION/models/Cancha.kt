package com.tucancha.data.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Modelo de Cancha para el microservicio Canchas (Puerto 8081)
 */
data class Cancha(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("descripcion")
    val descripcion: String? = null,
    
    @SerializedName("tipo")
    val tipo: String,  // "Fútbol", "Tenis", "Básquet", "Vóley"
    
    @SerializedName("precioPorHora")
    val precioPorHora: Double,  // Usando Double para simplicidad
    
    @SerializedName("direccion")
    val direccion: String,
    
    @SerializedName("ciudad")
    val ciudad: String? = null,
    
    @SerializedName("activa")
    val activa: Boolean? = true,
    
    @SerializedName("fechaCreacion")
    val fechaCreacion: String? = null,
    
    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String? = null
)

/**
 * Tipos de cancha disponibles
 */
object TipoCancha {
    const val FUTBOL = "Fútbol"
    const val TENIS = "Tenis"
    const val BASQUET = "Básquet"
    const val VOLEY = "Vóley"
    
    val todos = listOf(FUTBOL, TENIS, BASQUET, VOLEY)
}

