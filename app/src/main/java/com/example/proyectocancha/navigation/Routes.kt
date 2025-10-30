package com.example.proyectocancha.navigation

sealed class Routess(val path: String) {
    data object login : Routess("login")
    data object register : Routess("register")
    data object profile : Routess("profile")
    data object principal : Routess("principal")
    data object courtDetail : Routess("courtDetail")
    data object detalleReserva : Routess("detalleReserva")
    data object misReservas : Routess("misReservas")
    data object reciboReserva : Routess("reciboReserva")
    data object verCanchas : Routess("verCanchas")
    object admin : Routess("admin")
}