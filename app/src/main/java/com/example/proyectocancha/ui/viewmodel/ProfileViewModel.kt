package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado que representa la UI de la pantalla de perfil
data class ProfileState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val isLoading: Boolean = true,
    val isEditing: Boolean = false // Para controlar si se está editando
)

class ProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        // Simulamos la carga de datos del usuario al iniciar el ViewModel
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            // Aquí iría la lógica para obtener los datos del usuario actual
            // desde tu repositorio o base de datos.
            // Por ahora, usamos datos de ejemplo.
            _state.update {
                it.copy(
                    nombre = "Juan Pérez",
                    email = "juan.perez@example.com",
                    telefono = "+56 9 8765 4321",
                    isLoading = false
                )
            }
        }
    }

    // Activa o desactiva el modo de edición
    fun onEditModeChange(isEditing: Boolean) {
        _state.update { it.copy(isEditing = isEditing) }
    }

    // Actualiza el valor del teléfono en el estado
    fun onTelefonoChange(nuevoTelefono: String) {
        _state.update { it.copy(telefono = nuevoTelefono) }
    }

    // Guarda los cambios del teléfono
    fun savePhoneNumber() {
        viewModelScope.launch {
            // Aquí llamarías a tu repositorio para guardar el nuevo número
            // val success = userRepository.updatePhone(state.value.telefono)
            // if (success) { ... }

            // Al terminar, desactivamos el modo de edición
            _state.update { it.copy(isEditing = false) }
        }
    }

    // Lógica para cambiar la contraseña (más compleja, usualmente requiere re-autenticación)
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            // Lógica para cambiar la contraseña...
            // Esto es un placeholder. La implementación real es más compleja.
            println("Cambiando contraseña...")
        }
    }
}
