package com.example.proyectocancha.data.local.booking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: BookingEntity)

    @Query("SELECT * FROM bookings WHERE bookings.userId = :userId ORDER BY bookings.id DESC")
    suspend fun getBookingsForUser(userId: Int): List<BookingEntity>


    @Query("SELECT * FROM bookings WHERE bookings.courtId = :courtId AND bookings.day = :day AND bookings.status = 'Activa'")
    suspend fun getBookingsForCourtOnDay(courtId: Int, day: String): List<BookingEntity>

    @Query("UPDATE bookings SET status = :newStatus WHERE bookings.id = :bookingId")
    suspend fun updateBookingStatus(bookingId: Int, newStatus: String)

    @Query("SELECT COUNT(*) FROM bookings WHERE bookings.userId = :userId AND bookings.status = 'Activa'")
    suspend fun getActiveBookingCount(userId: Int): Int
}
