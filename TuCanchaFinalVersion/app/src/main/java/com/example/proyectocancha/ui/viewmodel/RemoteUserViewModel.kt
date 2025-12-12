package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.remote.UsuarioApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RemoteUserState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val usuariosList: List<com.example.proyectocancha.data.remote.dto.UsuarioDto> = emptyList()
)

class RemoteUserViewModel(
    private val usuarioApi: UsuarioApi
) : ViewModel() {

    private val _state = MutableStateFlow(RemoteUserState())
    val state = _state.asStateFlow()

    fun loadTodosLosUsuarios() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = usuarioApi.obtenerTodosLosUsuarios()
                if (response.isSuccessful && response.body() != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            usuariosList = response.body()!!,
                            errorMsg = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al cargar usuarios: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al cargar usuarios"
                    )
                }
            }
        }
    }

    fun eliminarUsuario(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = usuarioApi.eliminarUsuario(id)
                if (response.isSuccessful) {
                    // Recargar la lista después de eliminar
                    loadTodosLosUsuarios()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al eliminar usuario: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al eliminar usuario"
                    )
                }
            }
        }
    }

    fun cambiarRolUsuario(id: Long, nuevoRol: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = usuarioApi.cambiarRolUsuario(id, mapOf("rol" to nuevoRol))
                if (response.isSuccessful) {
                    // Recargar la lista después de cambiar rol
                    loadTodosLosUsuarios()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al cambiar rol: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al cambiar rol"
                    )
                }
            }
        }
    }
}

