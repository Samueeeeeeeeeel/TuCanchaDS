package com.example.proyectocancha.ui.model

import com.example.proyectocancha.R

// Definición de la clase de datos (El Modelo)
data class Court(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: Int,
    val description: String
)

// Lista de datos accesible globalmente (La Fuente de Datos)
val dummyCourts = listOf(
    // NOTA: Se ha añadido el precio (price) a cada cancha para la administración.
    Court(1, "Cancha Norte - Pasto Real", 20000, R.drawable.court_1,
        "Cancha con excelentes instalaciones y ambiente familiar."),
    Court(2, "Cancha Sur - Sintético", 22500, R.drawable.court_2,
        "Césped sintético de alta calidad, ideal para juegos rápidos y ligeros, techado."),
    Court(3, "Doble Cancha - Sintético", 30000, R.drawable.court_3
        , "Doble Cancha para competencias Amateurs, Excelentes luces para jugar en cualquier momento."),
    Court(4, "Cancha Pick - Baby Fut", 15000, R.drawable.court_4,
        "Cancha pequeña con iluminación profesional, especializada para baby fútbol."),
    Court(5, "Cancha Premium - VIP", 35000, R.drawable.court_5,
        "Cancha con vestuarios de lujo y servicio exclusivo."),
    Court(6, "Cancha Express - Rápida", 18000, R.drawable.court_6,
        "Cancha ideal para reservas de última hora.")
)
