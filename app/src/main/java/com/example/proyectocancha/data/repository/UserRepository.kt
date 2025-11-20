package com.example.proyectocancha.data.repository

import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.local.user.UserDao

// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO.
class UserRepository(
    private val userDao: UserDao // Inyección del DAO
) {

    // ----------------- LOGIN -----------------
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email) // Busca usuario por email
        return if (user != null && user.password == password) {
            Result.success(user) // Devuelve el usuario completo (incluye isAdmin)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    // ----------------- REGISTRO -----------------
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        isAdmin: Boolean = false
    ): Result<Long> {
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }

        val id = userDao.insert(
            UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = password,
                isAdmin = isAdmin
            )
        )
        return Result.success(id)
    }

    // ----------------- UPDATE USUARIO (para perfil) -----------------
    suspend fun updateUser(user: UserEntity): Result<Unit> { // <-- TIPO DE RETORNO CORREGIDO
        return try {
            userDao.update(user)
            Result.success(Unit) // <-- VALOR DE RETORNO CORREGIDO
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------- UTILIDADES -----------------
    suspend fun getAllUsers(): List<UserEntity> = userDao.getAll()

    suspend fun countUsers(): Int = userDao.count()
}
