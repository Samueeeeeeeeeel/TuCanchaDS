package com.example.proyectocancha.data.remote

import com.example.proyectocancha.data.remote.dto.LoginRequest
import com.example.proyectocancha.data.remote.dto.LoginResponse
import com.example.proyectocancha.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApi {
    
    @GET("api/usuarios")
    suspend fun obtenerTodosLosUsuarios(): Response<List<UsuarioDto>>
    
    @POST("api/usuarios")
    suspend fun registrarUsuario(@Body usuario: UsuarioDto): Response<UsuarioDto>
    
    @POST("api/usuarios/login")
    suspend fun login(@Body credenciales: LoginRequest): Response<LoginResponse>
    
    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<Unit>
    
    @PUT("api/usuarios/{id}/rol")
    suspend fun cambiarRolUsuario(
        @Path("id") id: Long,
        @Body rol: Map<String, String>
    ): Response<UsuarioDto>
}

