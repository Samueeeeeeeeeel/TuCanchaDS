package com.example.proyectocancha.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectocancha.data.local.booking.BookingDao
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.local.court.CourtDao
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.user.UserDao
import com.example.proyectocancha.data.local.user.UserEntity
import java.util.concurrent.Executors

@Database(
    entities = [UserEntity::class, CourtEntity::class, BookingEntity::class], // <-- AÑADIMOS BookingEntity
    version = 9, // <-- INCREMENTAMOS VERSIÓN
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courtDao(): CourtDao
    abstract fun bookingDao(): BookingDao // <-- AÑADIMOS BookingDao

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
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Executors.newSingleThreadExecutor().execute {
                            // Precarga de usuarios y canchas
                            db.execSQL("INSERT INTO users (name, email, phone, password, isAdmin) VALUES ('Admin', 'admin@duoc.cl', '+56911111111', 'Admin123!', 1)")
                            db.execSQL("INSERT INTO users (name, email, phone, password, isAdmin) VALUES ('Víctor Rosendo', 'victor@duoc.cl', '+56922222222', '123456', 0)")
                            db.execSQL("INSERT INTO courts (name, price, description, imageUrl) VALUES ('Cancha Norte - Pasto Real', 20000.0, 'Cancha con excelentes instalaciones y ambiente familiar.', '')")
                            db.execSQL("INSERT INTO courts (name, price, description, imageUrl) VALUES ('Cancha Sur - Sintético', 22500.0, 'Césped sintético de alta calidad, ideal para juegos rápidos y ligeros, techado.', '')")
                            db.execSQL("INSERT INTO courts (name, price, description, imageUrl) VALUES ('Doble Cancha - Sintético', 30000.0, 'Doble Cancha para competencias Amateurs, Excelentes luces para jugar en cualquier momento.', '')")
                            db.execSQL("INSERT INTO courts (name, price, description, imageUrl) VALUES ('Cancha Pick - Baby Fut', 15000.0, 'Cancha pequeña con iluminación profesional, especializada para baby fútbol.', '')")
                            db.execSQL("INSERT INTO courts (name, price, description, imageUrl) VALUES ('Cancha Premium - VIP', 35000.0, 'Cancha con vestuarios de lujo y servicio exclusivo.', '')")
                            db.execSQL("INSERT INTO courts (name, price, description, imageUrl) VALUES ('Cancha Express - Rápida', 18000.0, 'Cancha ideal para reservas de última hora.', '')")
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
