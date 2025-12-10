package com.tucancha.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tucancha.data.models.Cancha
import com.tucancha.data.repository.CanchaRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para pantallas de Canchas
 */
class CanchasViewModel(
    private val repository: CanchaRepository
) : ViewModel() {
    
    // Lista de canchas
    private val _canchas = MutableLiveData<List<Cancha>>()
    val canchas: LiveData<List<Cancha>> = _canchas
    
    // Cancha seleccionada
    private val _canchaSeleccionada = MutableLiveData<Cancha?>()
    val canchaSeleccionada: LiveData<Cancha?> = _canchaSeleccionada
    
    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Mensaje de error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        cargarCanchasActivas()
    }
    
    /**
     * Cargar todas las canchas activas
     */
    fun cargarCanchasActivas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.obtenerCanchasActivas()
            
            result.onSuccess { lista ->
                _canchas.value = lista
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Error al cargar canchas"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Buscar canchas por tipo
     */
    fun buscarPorTipo(tipo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.buscarPorTipo(tipo)
            
            result.onSuccess { lista ->
                _canchas.value = lista
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Error al buscar canchas"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Buscar canchas por ciudad
     */
    fun buscarPorCiudad(ciudad: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.buscarPorCiudad(ciudad)
            
            result.onSuccess { lista ->
                _canchas.value = lista
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Error al buscar canchas"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Seleccionar una cancha para ver detalles
     */
    fun seleccionarCancha(cancha: Cancha) {
        _canchaSeleccionada.value = cancha
    }
    
    /**
     * Cargar detalles de una cancha por ID
     */
    fun cargarCancha(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = repository.obtenerPorId(id)
            
            result.onSuccess { cancha ->
                _canchaSeleccionada.value = cancha
            }
            
            result.onFailure { exception ->
                _error.value = exception.message
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

