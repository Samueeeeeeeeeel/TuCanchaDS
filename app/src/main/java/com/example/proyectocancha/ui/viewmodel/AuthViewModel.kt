package com.example.proyectocancha.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow            // Estado observable mutable
import kotlinx.coroutines.flow.StateFlow                   // Exposición inmutable
import kotlinx.coroutines.flow.update                      // Helper para actualizar flows
import com.example.proyectocancha.ui.domain.validation.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val password: String = "",                                 // 4) Contraseña
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
    val password: String                                       // Contraseña en texto (solo demo; no producción)
)


class AuthViewModel: androidx.lifecycle.ViewModel() {
    companion object{
        private val USERS =mutableListOf(
            DemoUser(name = "Samuel", email = "a@a.cl", phone = "12345678", password = "Demo123!")
        )
    }
    // flujo de estadopara observar desde la UI
    private val _login = MutableStateFlow(LoginUistate()) //estado interno(login)

    val login: StateFlow<LoginUistate> = _login         // Exposición inmutable

    private val _register = MutableStateFlow(RegisterUistate()) //estado interno(register)

    val register :StateFlow<RegisterUistate> = _register // Exposición inmutable

    // handlres para el login(acciones que puede hacer el usuario)
    fun onLoginEmailChange(value: String){
        _login.update {it.copy(email = value, emailError = validarEmail(value))}
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String){
        _login.update {it.copy(password = value )}
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit(){
        val x = _login.value
        val cans = x.emailError == null && x.email.isNotBlank() &&
                x.password.isNotBlank()
        _login.update { it.copy(canSubmit = cans) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)

            // Buscamos en la **colección en memoria** un usuario con ese email
            val user = com.example.proyectocancha.ui.viewmodel.AuthViewModel.Companion.USERS.firstOrNull { it.email.equals(s.email, ignoreCase = true) }

            // ¿Coincide email + contraseña?
            val ok = user != null && user.password == s.password

            _login.update {
                it.copy(
                    isSubmitting = false,
                    success = ok,
                    errorMsg = if (!ok) "Credenciales inválidas" else null
                )
            }
        }
    }
    fun clearLoginResult(){
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(name = filtered, nameError = validarNombreSoloLetras(filtered))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validarEmail(value)) }
        recomputeRegisterCanSubmit()
    }
    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _register.update {
            it.copy(phone = digitsOnly, phoneError = validatePhoneisDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }
    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(password = value, passError = validarClaveFuerte(value)) }
        _register.update { it.copy(confirmError = validarConfirmacion(it.password, it.confirm)) }
        recomputeRegisterCanSubmit()
    }
    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validarConfirmacion(it.password, value)) }
        recomputeRegisterCanSubmit()
    }
    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null }
        val filled = s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.password.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(700)

            val duplicated = com.example.proyectocancha.ui.viewmodel.AuthViewModel.Companion.USERS.any { it.email.equals(s.email, ignoreCase = true) }

            if (duplicated) {
                _register.update {
                    it.copy(isSubmitting = false, success = false, errorMsg = "El usuario ya existe")
                }
                return@launch
            }


            com.example.proyectocancha.ui.viewmodel.AuthViewModel.Companion.USERS.add(
                DemoUser(
                    name = s.name.trim(),
                    email = s.email.trim(),
                    phone = s.phone.trim(),
                    password = s.password
                )
            )

            _register.update {
                it.copy(isSubmitting = false, success = true, errorMsg = null)
            }
        }
    }
    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

}