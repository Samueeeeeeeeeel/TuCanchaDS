package com.example.proyectocancha.data.local.database

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectocancha.R
import com.example.proyectocancha.data.local.booking.BookingDao
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.local.court.CourtDao
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.user.UserDao
import com.example.proyectocancha.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Database(
    entities = [UserEntity::class, CourtEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courtDao(): CourtDao
    abstract fun bookingDao(): BookingDao

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
                .addCallback(AppDatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                getInstance(context).let { database ->
                    prepopulateDatabase(context, database.userDao(), database.courtDao())
                }
            }
        }

        private fun copyDrawableToInternalStorage(context: Context, drawableId: Int, fileName: String): String {
            val directory = File(context.filesDir, "court_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val imageFile = File(directory, fileName)
            val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)

            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            return imageFile.absolutePath
        }

        suspend fun prepopulateDatabase(context: Context, userDao: UserDao, courtDao: CourtDao) {
            val admin = UserEntity(name = "Admin", email = "admin@duoc.cl", phone = "+56911111111", password = "Admin123!", isAdmin = true)
            val user = UserEntity(name = "Víctor Rosendo", email = "victor@duoc.cl", phone = "+56922222222", password = "123456", isAdmin = false)
            userDao.insert(admin)
            userDao.insert(user)

            val courts = listOf(
                CourtEntity(
                    name = "Cancha Norte - Pasto Real",
                    price = 20000.0,
                    description = "Cancha con excelentes instalaciones y ambiente familiar.",
                    imageUrl = copyDrawableToInternalStorage(context, R.drawable.court_1, "court_1.png")
                ),
                CourtEntity(
                    name = "Cancha Sur - Sintético",
                    price = 22500.0,
                    description = "Césped sintético de alta calidad, ideal para juegos rápidos y ligeros, techado.",
                    imageUrl = copyDrawableToInternalStorage(context, R.drawable.court_2, "court_2.png")
                ),
                CourtEntity(
                    name = "Doble Cancha - Sintético",
                    price = 30000.0,
                    description = "Doble Cancha para competencias Amateurs, Excelentes luces para jugar en cualquier momento.",
                    imageUrl = copyDrawableToInternalStorage(context, R.drawable.court_3, "court_3.png")
                ),
                CourtEntity(
                    name = "Cancha Pick - Baby Fut",
                    price = 15000.0,
                    description = "Cancha pequeña con iluminación profesional, especializada para baby fútbol.",
                    imageUrl = copyDrawableToInternalStorage(context, R.drawable.court_4, "court_4.png")
                ),
                CourtEntity(
                    name = "Cancha Premium - VIP",
                    price = 35000.0,
                    description = "Cancha con vestuarios de lujo y servicio exclusivo.",
                    imageUrl = copyDrawableToInternalStorage(context, R.drawable.court_5, "court_5.png")
                ),
                CourtEntity(
                    name = "Cancha Express - Rápida",
                    price = 18000.0,
                    description = "Cancha ideal para reservas de última hora.",
                    imageUrl = copyDrawableToInternalStorage(context, R.drawable.court_6, "court_6.png")
                )
            )
            courtDao.insertAll(courts)
        }
    }
}
