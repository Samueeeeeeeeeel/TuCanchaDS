package com.example.proyectocancha.data.local.booking

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

// Modelo de la reserva
data class Booking(
    val id: Int,
    val courtName: String,
    val date: String,
    val time: String,
    val total: Double,
    val status: String // "Activa", "Completada", "Cancelada"
)

/**
 * BookingManager mantiene la lista de reservas en memoria
 * mientras la app esté abierta.
 */
object BookingManager {

    // ✅ AHORA EMPIEZA VACÍA (sin reservas por defecto)
    val bookings: SnapshotStateList<Booking> = mutableStateListOf()

    fun cancelBooking(bookingId: Int) {
        val index = bookings.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val current = bookings[index]
            if (current.status == "Activa") {
                bookings[index] = current.copy(status = "Cancelada")
            }
        }
    }

    fun getActiveBookingsCount(): Int = bookings.count { it.status == "Activa" }

    fun addBooking(booking: Booking) {
        val index = bookings.indexOfFirst { it.id == booking.id }
        if (index == -1) {
            bookings.add(booking)
        } else {
            bookings[index] = booking
        }
    }
}
