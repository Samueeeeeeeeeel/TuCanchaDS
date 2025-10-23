package com.example.proyectocancha.ui.model

// Clase que representa una reserva. Es esencial para la pantalla de Administrador
// para listar y gestionar el estado de las reservas.
data class Reservation(
    val id: Int,
    val courtName: String,
    val userName: String,
    val time: String, // Representa la fecha y hora de la reserva (ej: "Hoy, 22 Oct 20:00")
    val status: String // Ej: "PENDIENTE", "CONFIRMADA", "CANCELADA"
)
