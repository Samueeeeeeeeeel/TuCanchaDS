package com.example.proyectocancha.data.repository

import com.example.proyectocancha.data.local.booking.BookingDao
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.local.user.AuthManager

class BookingRepository(private val bookingDao: BookingDao) {

    // Obtiene todas las reservas del usuario actualmente logueado
    suspend fun getMyBookings(): List<BookingEntity> {

        val userId = AuthManager.currentUser.value?.id?.toInt() ?: return emptyList()
        return bookingDao.getBookingsForUser(userId)
    }

    // Crea una nueva reserva para el usuario actual
    suspend fun addBooking(courtId: Int, courtName: String, day: String, time: String, total: Double) {
        val userId = AuthManager.currentUser.value?.id?.toInt()
        if (userId != null) {
            val newBooking = BookingEntity(
                userId = userId, // Se pasa el Int
                courtId = courtId,
                courtName = courtName,
                day = day,
                time = time,
                total = total,
                status = "Activa"
            )
            bookingDao.insert(newBooking)
        }
    }

    // Cancela una reserva
    suspend fun cancelBooking(bookingId: Int) {
        bookingDao.updateBookingStatus(bookingId, "Cancelada")
    }

    // Obtiene el contador de reservas activas para el usuario actual
    suspend fun getMyActiveBookingCount(): Int {
        val userId = AuthManager.currentUser.value?.id?.toInt() ?: return 0
        return bookingDao.getActiveBookingCount(userId)
    }

    // Obtiene las horas OCUPADAS para una cancha en un día específico
    suspend fun getBookedHoursForCourt(courtId: Int, day: String): List<String> {
        return bookingDao.getBookingsForCourtOnDay(courtId, day).map { it.time }
    }
}
