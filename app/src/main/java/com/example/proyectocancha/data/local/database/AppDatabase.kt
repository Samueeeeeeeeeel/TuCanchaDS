package com.example.proyectocancha.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.uinavegacion.data.local.user.UserDao
import com.example.proyectocancha.data.local.dao.CourtDao
import com.example.proyectocancha.ui.model.CourtEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, CourtEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courtDao(): CourtDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "proyectocancha.db"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `courts` (
                      `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      `name` TEXT NOT NULL,
                      `price` INTEGER NOT NULL,
                      `imageRes` INTEGER,
                      `imageUri` TEXT,
                      `description` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.userDao()?.let { dao ->
                                    val seed = listOf(
                                        UserEntity(
                                            name = "Admin",
                                            email = "admin@duoc.cl",
                                            phone = "+56911111111",
                                            password = "Admin123!",
                                            isAdmin = true
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
                    .fallbackToDestructiveMigration() // respaldo en desarrollo; puedes quitar si prefieres forzar migraciones
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}