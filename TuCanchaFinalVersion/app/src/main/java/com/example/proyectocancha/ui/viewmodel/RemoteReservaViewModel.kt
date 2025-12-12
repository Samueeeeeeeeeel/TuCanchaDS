package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.user.AuthManager
import com.example.proyectocancha.data.remote.ReservaApi
import com.example.proyectocancha.data.remote.dto.ReservaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class RemoteReservaState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val availableTimes: List<String> = emptyList(),
    val reservasList: List<ReservaDto> = emptyList()
)

class RemoteReservaViewModel(
    private val reservaApi: ReservaApi
) : ViewModel() {

    private val _state = MutableStateFlow(RemoteReservaState())
    val state = _state.asStateFlow()

    private val _bookingResult = MutableStateFlow<Result<ReservaDto>?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    fun loadAvailableTimes(canchaId: Long, fecha: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null, availableTimes = emptyList()) }
            try {
                // Obtener todas las reservas de la cancha
                val response = reservaApi.obtenerReservasPorCancha(canchaId)
                if (response.isSuccessful && response.body() != null) {
                    val reservas = response.body()!!
                    
                    // Convertir fecha de "dd/MM/yyyy" a formato para comparación
                    val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val selectedDate = inputFormat.parse(fecha)
                    
                    // Filtrar reservas del día seleccionado y que estén activas (no canceladas)
                    val reservasDelDia = reservas.filter { reserva ->
                        try {
                            val reservaDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                .parse(reserva.fechaInicio)
                            val reservaDateOnly = Calendar.getInstance().apply {
                                time = reservaDate ?: Date()
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val selectedDateOnly = Calendar.getInstance().apply {
                                time = selectedDate ?: Date()
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            reservaDateOnly.get(Calendar.YEAR) == selectedDateOnly.get(Calendar.YEAR) &&
                            reservaDateOnly.get(Calendar.DAY_OF_YEAR) == selectedDateOnly.get(Calendar.DAY_OF_YEAR) &&
                            reserva.estado != com.example.proyectocancha.data.remote.dto.EstadoReserva.CANCELADA
                        } catch (e: Exception) {
                            false
                        }
                    }
                    
                    // Generar todos los horarios posibles en formato "9:00-10:00" (9:00 a 21:00)
                    val todosLosHorarios = (9..21).map { hora ->
                        val horaInicio = String.format("%02d:00", hora)
                        val horaFin = String.format("%02d:00", hora + 1)
                        "$horaInicio-$horaFin"
                    }
                    
                    // Obtener horarios ocupados (solo la hora de inicio para comparar)
                    val horariosOcupados = reservasDelDia.mapNotNull { reserva ->
                        try {
                            val reservaDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                .parse(reserva.fechaInicio)
                            val calendar = Calendar.getInstance()
                            reservaDate?.let { calendar.time = it }
                            String.format("%02d:00", calendar.get(Calendar.HOUR_OF_DAY))
                        } catch (e: Exception) {
                            null
                        }
                    }.toSet()
                    
                    // Filtrar horarios disponibles (extraer solo la hora de inicio para comparar)
                    val horariosDisponibles = todosLosHorarios.filter { horario ->
                        val horaInicio = horario.substringBefore("-")
                        horaInicio !in horariosOcupados
                    }
                    
                    _state.update {
                        it.copy(
                            isLoading = false,
                            availableTimes = horariosDisponibles,
                            errorMsg = null
                        )
                    }
                } else {
                    // Si no hay reservas, todos los horarios están disponibles
                    val todosLosHorarios = (9..21).map { hora ->
                        val horaInicio = String.format("%02d:00", hora)
                        val horaFin = String.format("%02d:00", hora + 1)
                        "$horaInicio-$horaFin"
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            availableTimes = todosLosHorarios,
                            errorMsg = null
                        )
                    }
                }
            } catch (e: Exception) {
                // En caso de error, mostrar todos los horarios por defecto
                val todosLosHorarios = (9..21).map { hora ->
                    val horaInicio = String.format("%02d:00", hora)
                    val horaFin = String.format("%02d:00", hora + 1)
                    "$horaInicio-$horaFin"
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        availableTimes = todosLosHorarios,
                        errorMsg = null
                    )
                }
            }
        }
    }

    fun crearReserva(
        canchaId: Long,
        fecha: String, // Formato: "dd/MM/yyyy"
        hora: String, // Formato: "HH:mm-HH:mm" (ej: "9:00-10:00")
        precioPorHora: Int
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                // Extraer solo la hora de inicio del formato "9:00-10:00"
                val horaInicio = hora.substringBefore("-")
                
                // Convertir fecha y hora al formato ISO 8601
                val fechaInicio = convertirFechaHoraAISO(fecha, horaInicio)
                val fechaFin = calcularFechaFin(fecha, horaInicio)
                
                // Obtener el usuarioId del usuario logueado
                val usuarioId = AuthManager.currentUser.value?.id ?: throw Exception("Usuario no autenticado")
                
                val nuevaReserva = ReservaDto(
                    usuarioId = usuarioId,
                    canchaId = canchaId,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    precioTotal = precioPorHora.toDouble(),
                    estado = com.example.proyectocancha.data.remote.dto.EstadoReserva.PENDIENTE
                )
                
                val response = reservaApi.crearReserva(nuevaReserva)
                if (response.isSuccessful && response.body() != null) {
                    _bookingResult.value = Result.success(response.body()!!)
                    _state.update { it.copy(isLoading = false) }
                } else {
                    val error = Exception("Error al crear reserva: ${response.code()}")
                    _bookingResult.value = Result.failure(error)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al crear reserva: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _bookingResult.value = Result.failure(e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al crear reserva"
                    )
                }
            }
        }
    }

    fun loadReservasUsuario() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                // Obtener el usuarioId del usuario logueado
                val usuarioId = AuthManager.currentUser.value?.id 
                    ?: run {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMsg = "Usuario no autenticado"
                            )
                        }
                        return@launch
                    }
                
                val response = reservaApi.obtenerReservasPorUsuario(usuarioId)
                if (response.isSuccessful && response.body() != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            reservasList = response.body()!!,
                            errorMsg = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al cargar reservas: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al cargar reservas"
                    )
                }
            }
        }
    }

    fun cancelarReserva(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = reservaApi.cancelarReserva(id)
                if (response.isSuccessful) {
                    // Recargar la lista después de cancelar
                    loadReservasUsuario()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al cancelar reserva: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al cancelar reserva"
                    )
                }
            }
        }
    }

    fun clearResult() {
        _bookingResult.value = null
    }

    private fun convertirFechaHoraAISO(fecha: String, hora: String): String {
        // fecha formato: "dd/MM/yyyy", hora formato: "HH:mm"
        val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dateTime = inputFormat.parse("$fecha $hora")
        return outputFormat.format(dateTime ?: Date())
    }

    private fun calcularFechaFin(fecha: String, hora: String): String {
        // Asumimos que cada reserva es de 1 hora
        val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dateTime = inputFormat.parse("$fecha $hora")
        val calendar = Calendar.getInstance()
        dateTime?.let { calendar.time = it }
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        return outputFormat.format(calendar.time)
    }
}

