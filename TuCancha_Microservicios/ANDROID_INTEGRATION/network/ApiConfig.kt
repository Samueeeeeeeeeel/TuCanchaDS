package com.tucancha.data.network

/**
 * Configuración de URLs base para los microservicios
 * 
 * IMPORTANTE:
 * - Para EMULADOR Android: usar 10.0.2.2 (localhost del emulador)
 * - Para DISPOSITIVO FÍSICO: usar la IP de tu computadora (ej: 192.168.1.100)
 */
object ApiConfig {
    
    // ========================================
    // CONFIGURACIÓN PARA EMULADOR ANDROID
    // ========================================
    private const val BASE_HOST_EMULATOR = "10.0.2.2"
    
    // ========================================
    // CONFIGURACIÓN PARA DISPOSITIVO FÍSICO
    // Cambia esta IP por la de tu computadora
    // Para obtenerla: ipconfig (Windows) o ifconfig (Mac/Linux)
    // ========================================
    private const val BASE_HOST_DEVICE = "192.168.1.100"
    
    // ========================================
    // SELECCIONA CUÁL USAR (cambiar según necesites)
    // ========================================
    private const val CURRENT_HOST = BASE_HOST_EMULATOR  // Cambia a BASE_HOST_DEVICE si usas dispositivo físico
    
    // URLs de los microservicios
    const val BASE_URL_CANCHAS = "http://$CURRENT_HOST:8081/"
    const val BASE_URL_DISPONIBILIDAD = "http://$CURRENT_HOST:8082/"
    const val BASE_URL_LOGIN = "http://$CURRENT_HOST:8083/"
    const val BASE_URL_RESERVAS = "http://$CURRENT_HOST:8084/"
    
    // Timeouts en segundos
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}

