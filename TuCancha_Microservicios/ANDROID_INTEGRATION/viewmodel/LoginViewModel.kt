package com.tucancha.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tucancha.data.models.Usuario
import com.tucancha.data.repository.UsuarioRepository
import com.tucancha.data.session.UserSessionManager
import kotlinx.coroutines.launch

/**
 * ViewModel para pantallas de Login y Registro
 */
class LoginViewModel(
    private val repository: UsuarioRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {
    
    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Mensaje de error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Login exitoso
    private val _loginSuccess = MutableLiveData(false)
    val loginSuccess: LiveData<Boolean> = _loginSuccess
    
    // Registro exitoso
    private val _registroSuccess = MutableLiveData(false)
    val registroSuccess: LiveData<Boolean> = _registroSuccess
    
    /**
     * Iniciar sesión
     */
    fun login(email: String, password: String) {
        // Validaciones
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Email y contraseña son requeridos"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.login(email, password)
            
            result.onSuccess { response ->
                // Guardar sesión del usuario
                sessionManager.saveUserSession(response.usuario)
                _loginSuccess.value = true
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Error al iniciar sesión"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Registrar nuevo usuario
     */
    fun registrar(
        email: String,
        password: String,
        nombre: String,
        apellido: String?,
        telefono: String?
    ) {
        // Validaciones
        if (email.isBlank() || password.isBlank() || nombre.isBlank()) {
            _error.value = "Email, contraseña y nombre son requeridos"
            return
        }
        
        if (password.length < 6) {
            _error.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val usuario = Usuario(
                email = email,
                password = password,
                nombre = nombre,
                apellido = apellido,
                telefono = telefono
            )
            
            val result = repository.registrar(usuario)
            
            result.onSuccess {
                _registroSuccess.value = true
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Error al registrar usuario"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Cerrar sesión
     */
    fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
            _loginSuccess.value = false
        }
    }
    
    /**
     * Limpiar error
     */
    fun clearError() {
        _error.value = null
    }
}

