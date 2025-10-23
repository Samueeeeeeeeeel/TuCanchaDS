package com.example.proyectocancha.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.proyectocancha.ui.domain.validation.*
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ----------------------------------------------------------------------
// 1. ESTADOS DE UI (ACTUALIZADOS)
// ----------------------------------------------------------------------

data class LoginUistate(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val canSubmit: Boolean = false,
    val isSubmitting: Boolean = false,
    val success: Boolean = false,
    val isAdmin: Boolean = false, // <-- CAMBIO CLAVE: Rol de administrador
    val errorMsg: String? = null,
)

data class RegisterUistate(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirm: String = "",

    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

// ----------------------------------------------------------------------
// 2. MODELO DE USUARIO DEMO (ACTUALIZADO)
// ----------------------------------------------------------------------

private data class DemoUser(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val isAdmin: Boolean = false // <-- CAMBIO CLAVE: Campo de rol
)


class AuthViewModel: ViewModel() {
    companion object{
        // Añadido el campo isAdmin = true para el usuario administrador
        private val USERS =mutableListOf(
            DemoUser(name = "Samuel", email = "a@a.cl", phone = "12345678", password = "Demo123!"),
            DemoUser(name = "Admin",email ="admin@a.cl",phone ="789456123", password = "Admin123!", isAdmin = true) // <-- USUARIO ADMIN
        )
    }

    private val _login = MutableStateFlow(LoginUistate())
    val login: StateFlow<LoginUistate> = _login

    private val _register = MutableStateFlow(RegisterUistate())
    val register :StateFlow<RegisterUistate> = _register

    // ------------------------------------------------------------------
    // 3. HANDLERS DE LOGIN
    // ------------------------------------------------------------------

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
            // Limpiamos los resultados anteriores y activamos la carga
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false, isAdmin = false) }
            delay(500)

            // Buscamos al usuario
            val user = USERS.firstOrNull { it.email.equals(s.email, ignoreCase = true) }

            // Lógica de verificación
            val ok = user != null && user.password == s.password

            _login.update {
                it.copy(
                    isSubmitting = false,
                    success = ok,
                    // CORRECCIÓN: Usamos el isAdmin del usuario encontrado (o false por defecto)
                    isAdmin = user?.isAdmin ?: false,
                    errorMsg = if (!ok) "Credenciales inválidas" else null
                )
            }
        }
    }

    fun clearLoginResult(){
        // Al limpiar, mantenemos el estado de isAdmin si fue exitoso (aunque la UI navega inmediatamente)
        // pero reseteamos el éxito y el error.
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ------------------------------------------------------------------
    // 4. HANDLERS DE REGISTRO (SIN CAMBIOS)
    // ------------------------------------------------------------------

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

            val duplicated = USERS.any { it.email.equals(s.email, ignoreCase = true) }

            if (duplicated) {
                _register.update {
                    it.copy(isSubmitting = false, success = false, errorMsg = "El usuario ya existe")
                }
                return@launch
            }

            // Al registrar, el usuario NO es admin por defecto
            USERS.add(
                DemoUser(
                    name = s.name.trim(),
                    email = s.email.trim(),
                    phone = s.phone.trim(),
                    password = s.password,
                    isAdmin = false
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
