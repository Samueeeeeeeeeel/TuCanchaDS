package com.example.proyectocancha.data.local.booking

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val userId: Long,

    val courtName: String,
    val day: String,
    val time: String,

    // Nuevos campos para el recibo
    val subtotal: Double,
    val fee: Double,
    val total: Double,

    val status: String = "Activa"
)
