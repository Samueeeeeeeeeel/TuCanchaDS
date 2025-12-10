package com.tucancha.data.repository

import com.tucancha.data.models.Cancha
import com.tucancha.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones de Canchas
 * Maneja la comunicación con el microservicio de Canchas (Puerto 8081)
 */
class CanchaRepository {
    
    private val api = RetrofitClient.canchasApi
    
    /**
     * Obtener todas las canchas activas
     */
    suspend fun obtenerCanchasActivas(): Result<List<Cancha>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerActivas()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener canchas"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Obtener cancha por ID
     */
    suspend fun obtenerPorId(id: Long): Result<Cancha> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerPorId(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Cancha no encontrada"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Buscar canchas por tipo
     */
    suspend fun buscarPorTipo(tipo: String): Result<List<Cancha>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerPorTipo(tipo)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al buscar canchas por tipo"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Buscar canchas por ciudad
     */
    suspend fun buscarPorCiudad(ciudad: String): Result<List<Cancha>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerPorCiudad(ciudad)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al buscar canchas por ciudad"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

