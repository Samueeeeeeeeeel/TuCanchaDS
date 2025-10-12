package com.example.proyectocancha.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow            // Estado observable mutable
import kotlinx.coroutines.flow.StateFlow                   // Exposición inmutable
import kotlinx.coroutines.flow.update                      // Helper para actualizar flows
import com.example.proyectocancha.ui.domain.validation.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay

data class LoginUistate(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val canSubmit: Boolean = false,
    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
)

data class RegisterUistate(
    val name: String = "",                                 // 1) Nombre
    val email: String = "",                                // 2) Email
    val phone: String = "",                                // 3) Teléfono
    val pass: String = "",                                 // 4) Contraseña
    val confirm: String = "",                              // 5) Confirmación

    val nameError: String? = null,                         // Errores por campo
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null                           // Error global (ej: duplicado)
)

private data class DemoUser(                               // Datos que vamos a guardar en la colección
    val name: String,                                      // Nombre
    val email: String,                                     // Email (lo usamos como “id”)
    val phone: String,                                     // Teléfono
    val pass: String                                       // Contraseña en texto (solo demo; no producción)
)


class AuthViewModel: androidx.lifecycle.ViewModel() {
    companion object{
        private val USERS =mutableListOf(
            DemoUser(name = "Samuel", email = "a@a.cl", phone = "12345678", pass = "Demo123!")
        )
    }
    // flujo de estadopara observar desde la UI
    private val _login = MutableStateFlow(LoginUistate()) //estado interno(login)

    val login: StateFlow<LoginUistate> = _login         // Exposición inmutable

    private val _register = MutableStateFlow(RegisterUistate()) //estado interno(register)

    val register :StateFlow<RegisterUistate> = _register // Exposición inmutable

    // handlres para el login(acciones que puede hacer el usuario)
    fun onLoginEmailChange(value: String){
        _login.update {it.copy(email = value, emailError = validateEmail(value))}
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String){
        _login.update {it.copy(pass = value )}
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit(){
        val x = _login.value
        val cans = x.emailError == null && x.email.isNotBlank() &&
                x.pass.isNotBlank()
        _login.update { it.copy(canSubmit = cans) }
    }

    fun submitLogin(){
        val s =_login.value
        if (!s.canSubmit || s.isSubmitting) return //no hacer nada
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true,errorMsg = null, success = false) }
            delay(2000) // simular carga

            val user = USERS.find { it.email == s.email }
            if(user == null || user.pass != s.pass) {
                _login.update { it.copy(isSubmitting = false, errorMsg = "Credenciales incorrectas") }
            } else {
                _login.update { it.copy(isSubmitting = false, success = true) }
            }
        }
    }


}