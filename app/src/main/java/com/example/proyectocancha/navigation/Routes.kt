package com.example.proyectocancha.navigation

sealed class Routes(val path: String) { // <-- Nombre corregido
    data object login : Routes("login")
    data object register : Routes("register")
    data object profile : Routes("profile")
    data object principal : Routes("principal")
    data object courtDetail : Routes("courtDetail")
    data object misReservas : Routes("misReservas")
    data object reciboReserva : Routes("reciboReserva")
    data object verCanchas : Routes("verCanchas")
    data object admin : Routes("admin") // <-- Corregido a data object
}
