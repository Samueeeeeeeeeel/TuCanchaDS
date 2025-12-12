package com.tucancha.data.network

import com.tucancha.data.models.Disponibilidad
import retrofit2.Response
import retrofit2.http.*

/**
 * API para el Microservicio de Disponibilidad (Puerto 8082)
 * Base URL: http://10.0.2.2:8082/
 */
interface DisponibilidadApi {
    
    /**
     * Obtener todas las disponibilidades
     */
    @GET("api/disponibilidades")
    suspend fun obtenerTodas(): Response<List<Disponibilidad>>
    
    /**
     * Obtener disponibilidad por ID
     */
    @GET("api/disponibilidades/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<Disponibilidad>
    
    /**
     * Obtener disponibilidades activas de una cancha
     */
    @GET("api/disponibilidades/cancha/{canchaId}")
    suspend fun obtenerPorCancha(@Path("canchaId") canchaId: Long): Response<List<Disponibilidad>>
    
    /**
     * Obtener disponibilidades de una cancha en un rango de fechas
     */
    @GET("api/disponibilidades/cancha/{canchaId}/rango")
    suspend fun obtenerPorCanchaYRango(
        @Path("canchaId") canchaId: Long,
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): Response<List<Disponibilidad>>
    
    /**
     * Obtener disponibilidades disponibles en un rango de fechas
     */
    @GET("api/disponibilidades/rango")
    suspend fun obtenerDisponiblesPorRango(
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): Response<List<Disponibilidad>>
    
    /**
     * Verificar si una cancha está disponible
     * @return true si está disponible, false si no
     */
    @GET("api/disponibilidades/verificar")
    suspend fun verificar(
        @Query("canchaId") canchaId: Long,
        @Query("fechaInicio") fechaInicio: String,
        @Query("fechaFin") fechaFin: String
    ): Response<Boolean>
    
    /**
     * Crear nueva disponibilidad
     */
    @POST("api/disponibilidades")
    suspend fun crear(@Body disponibilidad: Disponibilidad): Response<Disponibilidad>
    
    /**
     * Actualizar disponibilidad existente
     */
    @PUT("api/disponibilidades/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body disponibilidad: Disponibilidad
    ): Response<Disponibilidad>
    
    /**
     * Eliminar disponibilidad
     */
    @DELETE("api/disponibilidades/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
    
    /**
     * Marcar como no disponible
     * @param motivo Razón por la que no está disponible
     */
    @PATCH("api/disponibilidades/{id}/no-disponible")
    suspend fun marcarNoDisponible(
        @Path("id") id: Long,
        @Query("motivo") motivo: String? = null
    ): Response<Unit>
}

