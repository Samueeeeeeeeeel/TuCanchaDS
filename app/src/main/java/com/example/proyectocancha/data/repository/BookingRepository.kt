package com.example.proyectocancha.data.repository

import com.example.proyectocancha.data.local.booking.BookingDao
import com.example.proyectocancha.data.local.booking.BookingEntity
import kotlinx.coroutines.flow.Flow

class BookingRepository(
    private val bookingDao: BookingDao
) {

    fun getBookingsForUser(userId: Long): Flow<List<BookingEntity>> =
        bookingDao.getBookingsForUser(userId)

    fun getActiveCountForUser(userId: Long): Flow<Int> =
        bookingDao.getActiveCountForUser(userId)

    suspend fun createBooking(booking: BookingEntity) {
        bookingDao.insertBooking(booking)
    }

    suspend fun cancelBooking(bookingId: Long) {
        bookingDao.cancelBooking(bookingId)
    }
}
