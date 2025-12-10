package com.tucancha.data.network

import com.tucancha.data.models.Cancha
import retrofit2.Response
import retrofit2.http.*

/**
 * API para el Microservicio de Canchas (Puerto 8081)
 * Base URL: http://10.0.2.2:8081/
 */
interface CanchasApi {
    
    /**
     * Obtener todas las canchas (activas e inactivas)
     */
    @GET("api/canchas")
    suspend fun obtenerTodas(): Response<List<Cancha>>
    
    /**
     * Obtener solo canchas activas
     */
    @GET("api/canchas/activas")
    suspend fun obtenerActivas(): Response<List<Cancha>>
    
    /**
     * Obtener cancha por ID
     */
    @GET("api/canchas/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<Cancha>
    
    /**
     * Buscar canchas por tipo de deporte
     * @param tipo "Fútbol", "Tenis", "Básquet", "Vóley"
     */
    @GET("api/canchas/tipo/{tipo}")
    suspend fun obtenerPorTipo(@Path("tipo") tipo: String): Response<List<Cancha>>
    
    /**
     * Buscar canchas activas por ciudad
     */
    @GET("api/canchas/ciudad/{ciudad}")
    suspend fun obtenerPorCiudad(@Path("ciudad") ciudad: String): Response<List<Cancha>>
    
    /**
     * Crear nueva cancha
     */
    @POST("api/canchas")
    suspend fun crear(@Body cancha: Cancha): Response<Cancha>
    
    /**
     * Actualizar cancha existente
     */
    @PUT("api/canchas/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body cancha: Cancha
    ): Response<Cancha>
    
    /**
     * Eliminar cancha permanentemente
     */
    @DELETE("api/canchas/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
    
    /**
     * Desactivar cancha (soft delete)
     */
    @PATCH("api/canchas/{id}/desactivar")
    suspend fun desactivar(@Path("id") id: Long): Response<Unit>
}

