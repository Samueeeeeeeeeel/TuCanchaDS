package com.tucancha.data.repository

import com.tucancha.data.models.EstadoReserva
import com.tucancha.data.models.Reserva
import com.tucancha.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para operaciones de Reservas
 * Maneja la comunicación con el microservicio de Reservas (Puerto 8084)
 */
class ReservaRepository {
    
    private val api = RetrofitClient.reservasApi
    
    /**
     * Obtener reservas de un usuario
     */
    suspend fun obtenerMisReservas(usuarioId: Long): Result<List<Reserva>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerPorUsuario(usuarioId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener reservas"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Obtener reservas de usuario por estado
     */
    suspend fun obtenerPorEstado(usuarioId: Long, estado: EstadoReserva): Result<List<Reserva>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.obtenerPorUsuarioYEstado(usuarioId, estado)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener reservas"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Verificar si una cancha está disponible
     */
    suspend fun verificarDisponibilidad(
        canchaId: Long,
        fechaInicio: String,
        fechaFin: String
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.verificarDisponibilidad(canchaId, fechaInicio, fechaFin)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al verificar disponibilidad"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Crear nueva reserva
     */
    suspend fun crearReserva(reserva: Reserva): Result<Reserva> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.crear(reserva)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "La cancha no está disponible en el horario seleccionado"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Confirmar reserva
     */
    suspend fun confirmarReserva(id: Long): Result<Reserva> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.confirmar(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al confirmar reserva"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Cancelar reserva
     */
    suspend fun cancelarReserva(id: Long, motivo: String? = null): Result<Reserva> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.cancelar(id, motivo)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al cancelar reserva"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

