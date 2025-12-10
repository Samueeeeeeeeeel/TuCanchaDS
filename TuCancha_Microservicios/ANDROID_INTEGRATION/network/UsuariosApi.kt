package com.tucancha.data.network

import com.tucancha.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API para el Microservicio de Login/Usuarios (Puerto 8083)
 * Base URL: http://10.0.2.2:8083/
 */
interface UsuariosApi {
    
    // ========================================
    // ENDPOINTS PÚBLICOS
    // ========================================
    
    /**
     * Obtener todos los usuarios
     */
    @GET("api/usuarios")
    suspend fun obtenerTodos(): Response<List<Usuario>>
    
    /**
     * Obtener usuario por ID
     */
    @GET("api/usuarios/{id}")
    suspend fun obtenerPorId(@Path("id") id: Long): Response<Usuario>
    
    /**
     * Obtener usuario por email
     */
    @GET("api/usuarios/email/{email}")
    suspend fun obtenerPorEmail(@Path("email") email: String): Response<Usuario>
    
    /**
     * Registrar nuevo usuario
     * Crea un usuario con rol USUARIO por defecto
     */
    @POST("api/usuarios")
    suspend fun registrar(@Body usuario: Usuario): Response<Usuario>
    
    /**
     * Iniciar sesión
     * @param credenciales Map con "email" y "password"
     * @return LoginResponse con mensaje y datos del usuario
     */
    @POST("api/usuarios/login")
    suspend fun login(@Body credenciales: LoginRequest): Response<LoginResponse>
    
    /**
     * Actualizar datos de usuario
     */
    @PUT("api/usuarios/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body usuario: Usuario
    ): Response<Usuario>
    
    /**
     * Eliminar usuario
     */
    @DELETE("api/usuarios/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
    
    /**
     * Desactivar usuario (soft delete)
     */
    @PATCH("api/usuarios/{id}/desactivar")
    suspend fun desactivar(@Path("id") id: Long): Response<Unit>
    
    // ========================================
    // ENDPOINTS DE ADMINISTRADOR
    // Requieren header "admin-email"
    // ========================================
    
    /**
     * Obtener todos los usuarios (Admin)
     */
    @GET("api/usuarios/admin/todos")
    suspend fun obtenerTodosAdmin(
        @Header("admin-email") adminEmail: String
    ): Response<List<Usuario>>
    
    /**
     * Obtener usuarios por rol (Admin)
     */
    @GET("api/usuarios/admin/rol/{rol}")
    suspend fun obtenerPorRol(
        @Path("rol") rol: String,
        @Header("admin-email") adminEmail: String
    ): Response<List<Usuario>>
    
    /**
     * Cambiar rol de usuario (Admin)
     */
    @PATCH("api/usuarios/admin/{id}/cambiar-rol")
    suspend fun cambiarRol(
        @Path("id") id: Long,
        @Query("nuevoRol") nuevoRol: String,
        @Header("admin-email") adminEmail: String
    ): Response<Usuario>
    
    /**
     * Eliminar usuario (Admin)
     */
    @DELETE("api/usuarios/admin/{id}")
    suspend fun eliminarAdmin(
        @Path("id") id: Long,
        @Header("admin-email") adminEmail: String
    ): Response<Unit>
    
    /**
     * Activar usuario (Admin)
     */
    @PATCH("api/usuarios/admin/{id}/activar")
    suspend fun activar(
        @Path("id") id: Long,
        @Header("admin-email") adminEmail: String
    ): Response<Usuario>
}

