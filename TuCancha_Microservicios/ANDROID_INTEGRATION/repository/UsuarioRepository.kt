package com.tucancha.data.repository

import com.tucancha.data.models.*
import com.tucancha.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones de Usuario
 * Maneja la comunicación con el microservicio de Login (Puerto 8083)
 */
class UsuarioRepository {
    
    private val api = RetrofitClient.usuariosApi
    
    /**
     * Iniciar sesión
     * @return Result con LoginResponse si éxito, o error si falla
     */
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Credenciales inválidas"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Registrar nuevo usuario
     */
    suspend fun registrar(usuario: Usuario): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.registrar(usuario)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Error al registrar usuario"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Obtener usuario por ID
     */
    suspend fun obtenerPorId(id: Long): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerPorId(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Usuario no encontrado"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Actualizar usuario
     */
    suspend fun actualizar(id: Long, usuario: Usuario): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.actualizar(id, usuario)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar usuario"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

