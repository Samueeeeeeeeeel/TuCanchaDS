package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.user.AuthManager
import com.example.proyectocancha.data.repository.UserRepository
import com.example.proyectocancha.domain.validation.validarClaveFuerte
import com.example.proyectocancha.domain.validation.validarConfirmacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val error: String? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val isSubmittable: Boolean = false,
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

class ChangePasswordViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    // --- VISIBILIDAD DE CONTRASEÑAS ---
    fun toggleCurrentPasswordVisibility() {
        _uiState.update { it.copy(isCurrentPasswordVisible = !it.isCurrentPasswordVisible) }
    }

    fun toggleNewPasswordVisibility() {
        _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onCurrentPasswordChange(password: String) {
        _uiState.update { it.copy(currentPassword = password, currentPasswordError = null) }
        recomputeIsSubmittable()
    }

    fun onNewPasswordChange(password: String) {
        val error = validarClaveFuerte(password)
        _uiState.update { it.copy(newPassword = password, newPasswordError = error) }
        recomputeIsSubmittable()
    }

    fun onConfirmPasswordChange(password: String) {
        val error = validarConfirmacion(_uiState.value.newPassword, password)
        _uiState.update { it.copy(confirmPassword = password, confirmPasswordError = error) }
        recomputeIsSubmittable()
    }

    private fun recomputeIsSubmittable() {
        val state = _uiState.value
        val canSubmit = state.currentPassword.isNotBlank() &&
                state.newPassword.isNotBlank() &&
                state.confirmPassword.isNotBlank() &&
                state.currentPasswordError == null &&
                state.newPasswordError == null &&
                state.confirmPasswordError == null
        _uiState.update { it.copy(isSubmittable = canSubmit) }
    }

    fun changePassword() {
        if (!_uiState.value.isSubmittable || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, currentPasswordError = null) }

            val currentUser = AuthManager.currentUser.value
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, error = "No se ha iniciado sesión.") }
                return@launch
            }

            if (currentUser.password != _uiState.value.currentPassword) {
                _uiState.update { it.copy(isLoading = false, currentPasswordError = "La contraseña actual es incorrecta.") }
                return@launch
            }

            val updatedUser = currentUser.copy(password = _uiState.value.newPassword)
            val result = userRepository.updateUser(updatedUser)

            if (result.isSuccess) {
                AuthManager.updateUser(updatedUser)
                _uiState.update { it.copy(isLoading = false, success = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al actualizar la contraseña.") }
            }
        }
    }
}

class ChangePasswordViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePasswordViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
