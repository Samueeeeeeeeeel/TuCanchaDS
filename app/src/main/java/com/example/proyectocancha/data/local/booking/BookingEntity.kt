package com.example.proyectocancha.data.local.booking


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userEmail: String,
    val courtName: String,
    val date: String,
    val time: String,
    val total: Double,
    val status: String = "Activa" // Activa, Completada, Cancelada
)
