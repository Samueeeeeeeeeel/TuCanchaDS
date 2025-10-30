package com.example.uinavegacion.data.local.user // (o donde esté tu archivo)

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectocancha.data.local.user.UserEntity

@Dao
interface UserDao {

    // Inserta un usuario
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    // ✅ ESTA ES LA FUNCIÓN CLAVE que usa el repositorio
    // Devuelve un usuario por email (o null si no existe).
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    // Cuenta total de usuarios
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    // Lista completa
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>
}