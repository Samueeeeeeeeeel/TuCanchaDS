package com.example.proyectocancha.ui.model

import com.example.proyectocancha.R

fun CourtEntity.toUiModel(): Court = Court(
    id = id.toInt(),
    name = name,
    price = price,
    imageUrl = imageRes ?: R.drawable.court_1, // usa drawable placeholder si imageRes es null
    description = description
)

// Convierte Court UI a entidad para insertar.
// Deja imageUri null (si quieres soportar subir imagen, rellénalo desde UI)
fun Court.toEntity(): CourtEntity = CourtEntity(
    id = 0L,
    name = name,
    price = price,
    imageRes = imageUrl,
    imageUri = null,
    description = description
)