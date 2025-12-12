package com.example.proyectocancha.data.remote

import com.example.proyectocancha.data.remote.dto.ReservaDto
import retrofit2.Response
import retrofit2.http.*

interface ReservaApi {
    
    @GET("api/reservas/usuario/{usuarioId}")
    suspend fun obtenerReservasPorUsuario(@Path("usuarioId") usuarioId: Long): Response<List<ReservaDto>>
    
    @GET("api/reservas/cancha/{canchaId}")
    suspend fun obtenerReservasPorCancha(@Path("canchaId") canchaId: Long): Response<List<ReservaDto>>
    
    @POST("api/reservas")
    suspend fun crearReserva(@Body reserva: ReservaDto): Response<ReservaDto>
    
    @PATCH("api/reservas/{id}/cancelar")
    suspend fun cancelarReserva(@Path("id") id: Long): Response<ReservaDto>
}

