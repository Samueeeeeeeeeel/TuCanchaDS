package com.example.proyectocancha.data.local.booking

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY id DESC")
    fun getBookingsForUser(userId: Long): Flow<List<BookingEntity>>

    @Query(
        "SELECT COUNT(*) FROM bookings " +
                "WHERE userId = :userId AND status = 'Activa'"
    )
    fun getActiveCountForUser(userId: Long): Flow<Int>

    @Query("UPDATE bookings SET status = 'Cancelada' WHERE id = :bookingId")
    suspend fun cancelBooking(bookingId: Long)
}
