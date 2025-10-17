package com.example.proyectocancha.ui.model

import com.example.proyectocancha.R

// Definición de la clase de datos
data class Court(val id: Int, val name: String, val imageUrl: Int, val description: String)

// Lista de datos accesible globalmente
val dummyCourts = listOf(
    Court(1, "Cancha Norte - Pasto Real", R.drawable.court_1, "Cancha con excelentes instalaciones y ambiente familiar."),
    Court(2, "Cancha Sur - Sintético", R.drawable.court_1, "Césped sintético de alta calidad, ideal para juegos rápidos y ligeros."),
    Court(3, "Cancha Valle - Sintético", R.drawable.court_1, "Cancha techada y climatizada, perfecta para jugar sin importar el clima."),
    Court(4, "Cancha Pick - Baby Fut", R.drawable.court_1, "Cancha pequeña con iluminación profesional, especializada para baby fútbol.")
)