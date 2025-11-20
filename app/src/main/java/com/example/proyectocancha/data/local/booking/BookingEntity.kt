package com.example.proyectocancha.data.local.booking

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.user.UserEntity

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourtEntity::class,
            parentColumns = ["id"],
            childColumns = ["courtId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,       // ID del usuario que reserva
    val courtId: Int,      // ID de la cancha reservada
    val courtName: String, // Nombre de la cancha (para mostrarlo fácilmente)
    val day: String,         // Fecha de la reserva (ej: "25/12/2024")
    val time: String,        // Rango de horas (ej: "18:00 - 19:00")
    val total: Double,     // Costo total de la reserva
    val status: String = "Activa" // Puede ser "Activa", "Completada", "Cancelada"
)
