package com.example.proyectocancha.data.remote

import com.example.proyectocancha.data.remote.dto.CanchaDto
import retrofit2.Response
import retrofit2.http.*

interface CanchaApi {
    
    @GET("api/canchas/activas")
    suspend fun obtenerCanchasActivas(): Response<List<CanchaDto>>
    
    @GET("api/canchas")
    suspend fun obtenerTodasLasCanchas(): Response<List<CanchaDto>>
    
    @GET("api/canchas/{id}")
    suspend fun obtenerCanchaPorId(@Path("id") id: Long): Response<CanchaDto>
    
    @POST("api/canchas")
    suspend fun crearCancha(@Body cancha: CanchaDto): Response<CanchaDto>
    
    @PUT("api/canchas/{id}")
    suspend fun actualizarCancha(
        @Path("id") id: Long,
        @Body cancha: CanchaDto
    ): Response<CanchaDto>
    
    @DELETE("api/canchas/{id}")
    suspend fun eliminarCancha(@Path("id") id: Long): Response<Unit>
}

