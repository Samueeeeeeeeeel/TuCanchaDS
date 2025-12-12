package com.tucancha.data.network

import com.tucancha.data.models.EstadoReserva
import com.tucancha.data.models.Reserva
import retrofit2.Response
import retrofit2.http.*

/**
 * API para el Microservicio de Reservas (Puerto 8084)
 * Base URL: http://10.0.2.2:8084/
 */
interface ReservasApi {
    
    /**
     * Obtener todas las reservas
     */
    @GET("api/reservas")
    suspend fun obtenerTodas(): Response<List<Reserva>>
    
    /**
     * Obtener reserva por ID
     */
    @GET("api/reservas/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<Reserva>
    
    /**
     * Obtener reservas de un usuario
     */
    @GET("api/reservas/usuario/{usuarioId}")
    suspend fun obtenerPorUsuario(@Path("usuarioId") usuarioId: Long): Response<List<Reserva>>
    
    /**
     * Obtener reservas de una cancha
     */
    @GET("api/reservas/cancha/{canchaId}")
    suspend fun obtenerPorCancha(@Path("canchaId") canchaId: Long): Response<List<Reserva>>
    
    /**
     * Obtener reservas por estado
     * @param estado PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA
     */
    @GET("api/reservas/estado/{estado}")
    suspend fun obtenerPorEstado(@Path("estado") estado: EstadoReserva): Response<List<Reserva>>
    
    /**
     * Obtener reservas de un usuario filtradas por estado
     */
    @GET("api/reservas/usuario/{usuarioId}/estado/{estado}")
    suspend fun obtenerPorUsuarioYEstado(
        @Path("usuarioId") usuarioId: Long,
        @Path("estado") estado: EstadoReserva
    ): Response<List<Reserva>>
    
    /**
     * Obtener reservas en un rango de fechas
     * @param fechaInicio Formato ISO 8601: "2024-01-15T10:00:00"
     * @param fechaFin Formato ISO 8601: "2024-01-15T12:00:00"
     */
    @GET("api/reservas/rango")
    suspend fun obtenerPorRango(
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): Response<List<Reserva>>
    
    /**
     * Verificar si una cancha está disponible para reservar
     * @return true si está disponible, false si hay conflicto
     */
    @GET("api/reservas/verificar")
    suspend fun verificarDisponibilidad(
        @Query("canchaId") canchaId: Long,
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): Response<Boolean>
    
    /**
     * Crear nueva reserva
     * Se crea en estado PENDIENTE por defecto
     */
    @POST("api/reservas")
    suspend fun crear(@Body reserva: Reserva): Response<Reserva>
    
    /**
     * Actualizar reserva existente
     */
    @PUT("api/reservas/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body reserva: Reserva
    ): Response<Reserva>
    
    /**
     * Eliminar reserva permanentemente
     */
    @DELETE("api/reservas/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
    
    /**
     * Confirmar reserva (cambia estado a CONFIRMADA)
     */
    @PATCH("api/reservas/{id}/confirmar")
    suspend fun confirmar(@Path("id") id: Long): Response<Reserva>
    
    /**
     * Cancelar reserva (cambia estado a CANCELADA)
     * @param motivo Motivo de cancelación (opcional)
     */
    @PATCH("api/reservas/{id}/cancelar")
    suspend fun cancelar(
        @Path("id") id: Long,
        @Query("motivo") motivo: String? = null
    ): Response<Reserva>
    
    /**
     * Completar reserva (cambia estado a COMPLETADA)
     */
    @PATCH("api/reservas/{id}/completar")
    suspend fun completar(@Path("id") id: Long): Response<Reserva>
}

