package com.example.proyectocancha.data.repository

import com.example.proyectocancha.data.local.booking.BookingDao
import com.example.proyectocancha.data.local.booking.BookingEntity

class BookingRepository(
    private val bookingDao: BookingDao
) {

    suspend fun insert(booking: BookingEntity) {
        bookingDao.insertBooking(booking)
    }

    suspend fun getUserBookings(email: String): List<BookingEntity> {
        return bookingDao.getBookingsByUser(email)
    }
}
