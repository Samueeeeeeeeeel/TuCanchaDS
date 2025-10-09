package com.example.proyectocancha.ui.domain.validation

import android.util.Patterns

//Validacion de correo: Formato, no vacio
fun validarEmail(email: String): String? {
    if (email.isBlank()) return "Por favor, ingresa un correo electrónico válido"
    //Validar email true o false
    val listo = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return  if(!listo) "Formato de Correo no valido" else null
}

//Validacion clave: no vacio, mayuscula, longitud
fun validarClaveFuerte(clave: String): String? {
    if (clave.isBlank()) return "Por favor, ingresa una contraseña"
    if (!clave.any { it.isUpperCase() }) return "Debe contener al menos una mayúscula"
    if (!clave.any { it.isLowerCase() }) return "Debe contener al menos una minúscula"
    if (clave.length < 8) return "La contraseña debe tener más de 7 caracteres"
    if (!clave.any { it.isLetterOrDigit() }) return "Debe contener al menos un símbolo"
    if (!clave.any { it.isDigit() }) return "Debe contener al menos un número"
    return null
}

//Validar nombre: no vacio, solo letras
fun validarNombreSoloLetras(nombre: String): String? {
    if (nombre.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(nombre)) "Debe contener solo letras" else null
}

//Validar telefono
fun validatePhoneisDigitsOnly(phone: String): String?{
    if ((phone.isBlank())) return "Debe escribir su número de teléfono"
    if(!phone.all { it.isDigit() }) return "Debe ingresar solo números"
    if(phone.length !in 8 .. 9) return "Debe tener entre 8 y 9 digitos"
    return null
}

//Validar contraseñas iguales
fun validarConfirmacion(clave: String, confirmacion: String): String? {
    if (confirmacion.isBlank()) return "Debe confirmar su contraseña"
    return if (clave != confirmacion) "Las contraseñas no coinciden" else null
}

