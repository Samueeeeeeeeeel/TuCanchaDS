package com.example.proyectocancha.ui.model


import com.example.proyectocancha.R

// CLASES DE DATOS CENTRALIZADAS
data class Court(val id: Int, val name: String, val imageUrl: Int, val description: String)
data class Promotion(val id: Int, val title: String, val imageUrl: Int, val ctaText: String)

val dummyPromotions = listOf(
    Promotion(1, "¡Promoción Destacada!", R.drawable.court_1, "Reservar"),
    Promotion(2, "Descuento de Verano", R.drawable.court_1, "Ver Oferta")
)

val dummyCourts = listOf(
    Court(1, "Cancha Principal A", R.drawable.court_1, "Cancha con excelentes instalaciones"),
    Court(2, "Cancha B - Sintético", R.drawable.court_1, "Césped sintético de alta calidad"),
    Court(3, "Cancha C - Interior", R.drawable.court_1, "Cancha techada climatizada")
)