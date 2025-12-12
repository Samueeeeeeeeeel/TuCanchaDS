package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.user.AuthManager
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.domain.validation.validarClaveFuerte
import com.example.proyectocancha.domain.validation.validarConfirmacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,

    // Estado para el diálogo de cambio de contraseña
    val showPasswordDialog: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",

    // --- NUEVO: Estado para los errores de contraseña ---
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val passwordChangeSuccess: Boolean = false
)

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            AuthManager.currentUser.collect { user ->
                if (user != null) {
                    _state.update {
                        it.copy(
                            nombre = user.name,
                            email = user.email,
                            telefono = user.phone,
                            isAdmin = user.isAdmin,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { ProfileState(isLoading = false) }
                }
            }
        }
    }

    fun onEditModeChange(isEditing: Boolean) {
        _state.update { it.copy(isEditing = isEditing) }
    }

    fun onNombreChange(nuevoNombre: String) {
        _state.update { it.copy(nombre = nuevoNombre) }
    }

    fun onTelefonoChange(nuevoTelefono: String) {
        _state.update { it.copy(telefono = nuevoTelefono) }
    }

    fun saveProfileChanges() {
        viewModelScope.launch {
            AuthManager.currentUser.value?.let { currentUser ->
                val updatedUser = currentUser.copy(
                    name = state.value.nombre,
                    phone = state.value.telefono
                )
                val result = userRepository.updateUser(updatedUser)
                if (result.isSuccess) {
                    AuthManager.updateUser(updatedUser)
                }
            }
            _state.update { it.copy(isEditing = false) }
        }
    }

    fun onShowPasswordDialog(show: Boolean) {
        // Limpiamos los errores al abrir/cerrar el diálogo
        _state.update { it.copy(showPasswordDialog = show, currentPasswordError = null, newPasswordError = null, confirmPasswordError = null, passwordChangeSuccess = false) }
    }

    fun onCurrentPasswordChange(password: String) {
        _state.update { it.copy(currentPassword = password, currentPasswordError = null) }
    }

    fun onNewPasswordChange(password: String) {
        val error = validarClaveFuerte(password)
        _state.update { it.copy(newPassword = password, newPasswordError = error) }
    }

    fun onConfirmPasswordChange(password: String) {
        val error = validarConfirmacion(state.value.newPassword, password)
        _state.update { it.copy(confirmPassword = password, confirmPasswordError = error) }
    }

    fun changePassword() {
        viewModelScope.launch {
            val currentState = state.value
            val currentUser = AuthManager.currentUser.value

            if (currentUser == null) return@launch

            // 1. Validar contraseña actual
            if (currentUser.password != currentState.currentPassword) {
                _state.update { it.copy(currentPasswordError = "La contraseña actual es incorrecta") }
                return@launch
            }

            // 2. Validar nueva contraseña y confirmación (los errores ya están en el estado)
            if (currentState.newPasswordError != null || currentState.confirmPasswordError != null) {
                return@launch
            }

            // 3. Si todo está OK, actualizar
            val updatedUser = currentUser.copy(password = currentState.newPassword)
            val result = userRepository.updateUser(updatedUser)

            if (result.isSuccess) {
                AuthManager.updateUser(updatedUser)
                _state.update {
                    it.copy(
                        showPasswordDialog = false,
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        passwordChangeSuccess = true
                    )
                }
            }
        }
    }
}
