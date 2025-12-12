package com.example.proyectocancha.data.remote

/**
 * Configuración centralizada de URLs para los microservicios
 * 
 * INSTRUCCIONES:
 * 1. Para EMULADOR Android: usa "10.0.2.2" (apunta a localhost de tu PC)
 * 2. Para DISPOSITIVO FÍSICO: usa la IP local de tu computadora
 *    - Windows: ejecuta "ipconfig" en CMD y busca "IPv4 Address"
 *    - Ejemplo: "192.168.1.100"
 * 3. Para OTRA PC en la misma red: usa la IP de esa PC
 * 
 * IMPORTANTE: Asegúrate de que:
 * - Los microservicios estén corriendo en la PC objetivo
 * - El firewall permita conexiones en los puertos 8081, 8083, 8084
 * - Android y la PC estén en la misma red WiFi
 */
object ApiConfig {
    
    // ========================================
    // CONFIGURACIÓN PARA EMULADOR ANDROID
    // ========================================
    private const val BASE_HOST_EMULATOR = "10.0.2.2"
    
    // ========================================
    // CONFIGURACIÓN PARA DISPOSITIVO FÍSICO / OTRA PC
    // Cambia esta IP por la de tu computadora o la PC donde corren los microservicios
    // Para obtener tu IP: ipconfig (Windows) o ifconfig (Mac/Linux)
    // ========================================
    private const val BASE_HOST_DEVICE = "192.168.1.100"  // ⚠️ CAMBIA ESTA IP
    
    // ========================================
    // SELECCIONA CUÁL USAR
    // Cambia a BASE_HOST_DEVICE si usas dispositivo físico o otra PC
    // ========================================
    private const val CURRENT_HOST = BASE_HOST_EMULATOR  // Cambia a BASE_HOST_DEVICE si es necesario
    
    // URLs de los microservicios
    const val BASE_URL_CANCHAS = "http://$CURRENT_HOST:8081/"
    const val BASE_URL_USUARIOS = "http://$CURRENT_HOST:8083/"
    const val BASE_URL_RESERVAS = "http://$CURRENT_HOST:8084/"
    
    // Timeouts en segundos
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}

