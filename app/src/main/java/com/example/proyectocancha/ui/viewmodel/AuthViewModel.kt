package com.example.proyectocancha.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.proyectocancha.ui.domain.validation.*
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.uinavegacion.data.repository.UserRepository
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
    val isAdmin: Boolean = false,
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

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUistate())
    val login: StateFlow<LoginUistate> = _login

    private val _register = MutableStateFlow(RegisterUistate())
    val register: StateFlow<RegisterUistate> = _register

    // ----------------- LOGIN -----------------

    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validarEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(password = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null && s.email.isNotBlank() && s.password.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false, isAdmin = false) }
            delay(500)

            val result = repository.login(s.email.trim(), s.password)

            _login.update {
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        isAdmin = user?.isAdmin == true,
                        errorMsg = null
                    )
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación"
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO -----------------

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

            val result = repository.register(
                name = s.name.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),
                password = s.password
            )

            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar"
                    )
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }
}