package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.remote.UsuarioApi
import com.example.proyectocancha.data.remote.dto.LoginRequest
import com.example.proyectocancha.data.remote.dto.UsuarioDto
import com.example.proyectocancha.domain.validation.validarClaveFuerte
import com.example.proyectocancha.domain.validation.validarConfirmacion
import com.example.proyectocancha.domain.validation.validarEmail
import com.example.proyectocancha.domain.validation.validarNombreSoloLetras
import com.example.proyectocancha.domain.validation.validatePhoneisDigitsOnly
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RemoteAuthViewModel(
    private val usuarioApi: UsuarioApi
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
            _login.update {
                it.copy(
                    isSubmitting = true,
                    errorMsg = null,
                    success = false,
                    isAdmin = false,
                    user = null
                )
            }

            try {
                val loginRequest = LoginRequest(
                    email = s.email.trim(),
                    password = s.password
                )
                
                val response = usuarioApi.login(loginRequest)
                
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val usuarioDto = loginResponse.usuario
                    
                    // Convertir UsuarioDto a UserEntity
                    val userEntity = convertirUsuarioDtoAUserEntity(usuarioDto)
                    
                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            success = true,
                            errorMsg = null,
                            isAdmin = usuarioDto.rol == "ADMIN",
                            user = userEntity
                        )
                    }
                } else {
                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = "Credenciales inválidas",
                            user = null
                        )
                    }
                }
            } catch (e: Exception) {
                _login.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = e.message ?: "Error de autenticación",
                        user = null
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update {
            it.copy(
                success = false,
                errorMsg = null,
                user = null
            )
        }
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
        _register.update {
            it.copy(
                confirm = value,
                confirmError = validarConfirmacion(it.password, value)
            )
        }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors =
            listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null }
        val filled =
            s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() &&
                    s.password.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            
            try {
                // Dividir nombre completo en nombre y apellido
                val partesNombre = s.name.trim().split(" ", limit = 2)
                val nombre = partesNombre[0]
                val apellido = if (partesNombre.size > 1) partesNombre[1] else null
                
                val nuevoUsuario = UsuarioDto(
                    nombre = nombre,
                    apellido = apellido,
                    email = s.email.trim(),
                    telefono = s.phone.trim(),
                    password = s.password,
                    rol = "USUARIO",
                    activo = true
                )
                
                val response = usuarioApi.registrarUsuario(nuevoUsuario)
                
                if (response.isSuccessful && response.body() != null) {
                    _register.update { current ->
                        current.copy(isSubmitting = false, success = true, errorMsg = null)
                    }
                } else {
                    val errorMsg = try {
                        response.errorBody()?.string() ?: "Error desconocido"
                    } catch (e: Exception) {
                        "Error al registrar: ${response.code()}"
                    }
                    _register.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = errorMsg
                        )
                    }
                }
            } catch (e: Exception) {
                _register.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = e.message ?: "No se pudo registrar"
                    )
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // Función auxiliar para convertir UsuarioDto a UserEntity
    private fun convertirUsuarioDtoAUserEntity(usuarioDto: UsuarioDto): UserEntity {
        val nombreCompleto = if (usuarioDto.apellido != null) {
            "${usuarioDto.nombre} ${usuarioDto.apellido}"
        } else {
            usuarioDto.nombre
        }
        
        return UserEntity(
            id = usuarioDto.id ?: 0L,
            name = nombreCompleto,
            email = usuarioDto.email,
            phone = usuarioDto.telefono ?: "",
            password = "", // No guardamos la contraseña en UserEntity
            isAdmin = usuarioDto.rol == "ADMIN"
        )
    }
}

