package com.example.proyectocancha.data.local.court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courts")
data class CourtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val imageUrl: String, // Usaremos un string para la URL o path de la imagen
    val description: String
)
