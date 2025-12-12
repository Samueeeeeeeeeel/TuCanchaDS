package com.tucancha.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit singleton para cada microservicio
 */
object RetrofitClient {
    
    // Logging para debug (ver requests/responses en Logcat)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Cliente OkHttp con configuración
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()
    
    // ========================================
    // RETROFIT PARA MICROSERVICIO CANCHAS
    // ========================================
    private val retrofitCanchas: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL_CANCHAS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val canchasApi: CanchasApi by lazy {
        retrofitCanchas.create(CanchasApi::class.java)
    }
    
    // ========================================
    // RETROFIT PARA MICROSERVICIO DISPONIBILIDAD
    // ========================================
    private val retrofitDisponibilidad: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL_DISPONIBILIDAD)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val disponibilidadApi: DisponibilidadApi by lazy {
        retrofitDisponibilidad.create(DisponibilidadApi::class.java)
    }
    
    // ========================================
    // RETROFIT PARA MICROSERVICIO LOGIN
    // ========================================
    private val retrofitLogin: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL_LOGIN)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val usuariosApi: UsuariosApi by lazy {
        retrofitLogin.create(UsuariosApi::class.java)
    }
    
    // ========================================
    // RETROFIT PARA MICROSERVICIO RESERVAS
    // ========================================
    private val retrofitReservas: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL_RESERVAS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val reservasApi: ReservasApi by lazy {
        retrofitReservas.create(ReservasApi::class.java)
    }
}

