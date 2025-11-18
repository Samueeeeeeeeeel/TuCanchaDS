package com.example.proyectocancha.data.local.booking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookingDao {

    @Insert
    suspend fun insertBooking(booking: BookingEntity)

    @Query("SELECT * FROM bookings WHERE userEmail = :email ORDER BY id DESC")
    suspend fun getBookingsByUser(email: String): List<BookingEntity>
}
