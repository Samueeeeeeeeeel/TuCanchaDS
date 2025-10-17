package com.example.proyectocancha.ui.navigation

import androidx.compose.ui.graphics.Path

//Clse sellada para definir las rutas de navegación
sealed class Routess(val path: String) {
    data object home : Routess("home")
    data object login : Routess("login")
    data object register : Routess("register")
    data object  profile : Routess("profile")
    data object principal : Routess("principal")
    data object courtDetail : Routess("courtDetail")
    data object detalleReserva : Routess("detalleReserva")
    data object misReservas : Routess("misReservas")
    data object reciboReserva : Routess("reciboReserva")
    data object verCanchas : Routess("verCanchas")
}

