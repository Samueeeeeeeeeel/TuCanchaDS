package com.example.proyectocancha.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectocancha.data.local.booking.BookingDao
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.local.court.CourtDao
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.user.UserDao
import com.example.proyectocancha.data.local.user.UserEntity

@Database(
    entities = [
        UserEntity::class,
        BookingEntity::class,
        CourtEntity::class
    ],
    // 👈 IMPORTANTE: sube la versión (si antes era 1, ahora 2)
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bookingDao(): BookingDao
    abstract fun courtDao(): CourtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proyecto_cancha_db"
                )
                    // 👇 Para no complicarnos con migraciones en este proyecto:
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
