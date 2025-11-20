package com.example.proyectocancha.data.repository

import android.util.Log
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.local.user.UserDao

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        Log.d("LOGIN_DEBUG", "Intentando login con email: '$email'")
        val user = userDao.getByEmail(email)

        if (user == null) {
            Log.e("LOGIN_DEBUG", "Error: No se encontró ningún usuario con el email: '$email'")
            return Result.failure(IllegalArgumentException("Credenciales inválidas (usuario no encontrado)"))
        } else {
            Log.d("LOGIN_DEBUG", "Usuario encontrado: ${user.name}. Verificando contraseña...")
            Log.d("LOGIN_DEBUG", "Contraseña proporcionada: '$password'")
            Log.d("LOGIN_DEBUG", "Contraseña en BD: '${user.password}'")
            
            return if (user.password == password) {
                Log.d("LOGIN_DEBUG", "¡Éxito! Las contraseñas coinciden.")
                Result.success(user)
            } else {
                Log.e("LOGIN_DEBUG", "Error: Las contraseñas NO coinciden.")
                Result.failure(IllegalArgumentException("Credenciales inválidas (contraseña incorrecta)"))
            }
        }
    }

    // --- ¡LÓGICA DE REGISTRO A PRUEBA DE CRASHES! ---
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        isAdmin: Boolean = false
    ): Result<Long> {

        // 1. VERIFICAR SI EL USUARIO YA EXISTE
        val existingUser = userDao.getByEmail(email)
        if (existingUser != null) {
            // Si existe, devolvemos un error controlado en lugar de crashear.
            return Result.failure(IllegalStateException("El correo electrónico ya está registrado."))
        }

        // 2. Si no existe, procedemos a crear el nuevo usuario.
        val newUser = UserEntity(
            name = name,
            email = email,
            phone = phone,
            password = password,
            isAdmin = isAdmin
        )
        
        val id = userDao.insert(newUser)
        return Result.success(id)
    }

    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.update(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): List<UserEntity> = userDao.getAll()

    suspend fun countUsers(): Int = userDao.count()
}
