package com.tucancha.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tucancha.data.models.EstadoReserva
import com.tucancha.data.models.Reserva
import com.tucancha.data.repository.ReservaRepository
import com.tucancha.data.session.UserSessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ViewModel para pantallas de Reservas
 */
class ReservasViewModel(
    private val repository: ReservaRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {
    
    // Lista de reservas del usuario
    private val _misReservas = MutableLiveData<List<Reserva>>()
    val misReservas: LiveData<List<Reserva>> = _misReservas
    
    // Disponibilidad verificada
    private val _disponible = MutableLiveData<Boolean?>()
    val disponible: LiveData<Boolean?> = _disponible
    
    // Reserva creada/actualizada
    private val _reservaCreada = MutableLiveData<Reserva?>()
    val reservaCreada: LiveData<Reserva?> = _reservaCreada
    
    // Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Mensaje de error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // Mensaje de éxito
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    /**
     * Cargar mis reservas
     */
    fun cargarMisReservas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val usuarioId = sessionManager.getUserIdSync()
            if (usuarioId == 0L) {
                _error.value = "No hay usuario logueado"
                _isLoading.value = false
                return@launch
            }
            
            val result = repository.obtenerMisReservas(usuarioId)
            
            result.onSuccess { lista ->
                _misReservas.value = lista
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Error al cargar reservas"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Cargar reservas por estado
     */
    fun cargarReservasPorEstado(estado: EstadoReserva) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val usuarioId = sessionManager.getUserIdSync()
            val result = repository.obtenerPorEstado(usuarioId, estado)
            
            result.onSuccess { lista ->
                _misReservas.value = lista
            }
            
            result.onFailure { exception ->
                _error.value = exception.message
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Verificar disponibilidad de una cancha
     */
    fun verificarDisponibilidad(
        canchaId: Long,
        fechaInicio: LocalDateTime,
        fechaFin: LocalDateTime
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _disponible.value = null
            
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val result = repository.verificarDisponibilidad(
                canchaId,
                fechaInicio.format(formatter),
                fechaFin.format(formatter)
            )
            
            result.onSuccess { disponible ->
                _disponible.value = disponible
                if (!disponible) {
                    _error.value = "La cancha no está disponible en el horario seleccionado"
                }
            }
            
            result.onFailure { exception ->
                _error.value = exception.message
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Crear nueva reserva
     */
    fun crearReserva(
        canchaId: Long,
        fechaInicio: LocalDateTime,
        fechaFin: LocalDateTime,
        precioTotal: Double,
        observaciones: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val usuarioId = sessionManager.getUserIdSync()
            if (usuarioId == 0L) {
                _error.value = "No hay usuario logueado"
                _isLoading.value = false
                return@launch
            }
            
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            
            val reserva = Reserva(
                usuarioId = usuarioId,
                canchaId = canchaId,
                fechaInicio = fechaInicio.format(formatter),
                fechaFin = fechaFin.format(formatter),
                precioTotal = precioTotal,
                observaciones = observaciones
            )
            
            val result = repository.crearReserva(reserva)
            
            result.onSuccess { nuevaReserva ->
                _reservaCreada.value = nuevaReserva
                _successMessage.value = "Reserva creada exitosamente"
                cargarMisReservas() // Recargar lista
            }
            
            result.onFailure { exception ->
                _error.value = exception.message ?: "Error al crear reserva"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Confirmar reserva
     */
    fun confirmarReserva(reservaId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = repository.confirmarReserva(reservaId)
            
            result.onSuccess {
                _successMessage.value = "Reserva confirmada"
                cargarMisReservas()
            }
            
            result.onFailure { exception ->
                _error.value = exception.message
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Cancelar reserva
     */
    fun cancelarReserva(reservaId: Long, motivo: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = repository.cancelarReserva(reservaId, motivo)
            
            result.onSuccess {
                _successMessage.value = "Reserva cancelada"
                cargarMisReservas()
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
    
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}

