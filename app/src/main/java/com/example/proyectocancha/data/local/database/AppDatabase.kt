package com.example.proyectocancha.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.uinavegacion.data.local.user.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false // más simple, no genera archivos de esquema
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "proyectocancha.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Precarga de usuarios iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.userDao()?.let { dao ->
                                    val seed = listOf(
                                        UserEntity(
                                            name = "Admin",
                                            email = "admin@duoc.cl",
                                            phone = "+56911111111",
                                            password = "Admin123!",
                                            isAdmin = true // ⚡ importante
                                        ),
                                        UserEntity(
                                            name = "Víctor Rosendo",
                                            email = "victor@duoc.cl",
                                            phone = "+56922222222",
                                            password = "123456",
                                            isAdmin = false
                                        )
                                    )
                                    if (dao.count() == 0) {
                                        seed.forEach { dao.insert(it) }
                                    }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}