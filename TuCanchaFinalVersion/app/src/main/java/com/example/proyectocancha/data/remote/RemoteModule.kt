package com.example.proyectocancha.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RemoteModule {
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
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
    
    val canchaApi: CanchaApi by lazy {
        retrofitCanchas.create(CanchaApi::class.java)
    }
    
    // ========================================
    // RETROFIT PARA MICROSERVICIO USUARIOS/LOGIN
    // ========================================
    private val retrofitUsuarios: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL_USUARIOS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val usuarioApi: UsuarioApi by lazy {
        retrofitUsuarios.create(UsuarioApi::class.java)
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
    
    val reservaApi: ReservaApi by lazy {
        retrofitReservas.create(ReservaApi::class.java)
    }
}