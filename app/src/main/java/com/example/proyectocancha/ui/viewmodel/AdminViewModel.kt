// ARCHIVO: AdminViewModel.kt
// UBICACIÓN: app/src/main/java/com/example/proyectocancha/ui/viewmodel/

package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// ----------------------------------------------------------------------
// ESTADO DE LA UI DE ADMINISTRACIÓN
// ----------------------------------------------------------------------

data class AdminUiState(
    val courtList: List<Court> = emptyList(),
    val reservationList: List<Reservation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state

    // ID de recurso dummy para la imagen de la cancha (Reemplaza 0 con R.drawable.court_1 si es necesario)
    private val DUMMY_IMAGE_ID = 0
    private val DEFAULT_DESCRIPTION = "Cancha estándar administrable."

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _state.update { it.copy(isLoading = true) }

        // 1. Canchas simuladas (utilizando el constructor completo de 5 campos)
        val mockCourts = mutableListOf(
            Court(id = 1, name = "Cancha Norte - Pasto Real", price = 20.0, imageUrl = DUMMY_IMAGE_ID, description = "Cancha con excelentes instalaciones y ambiente familiar."),
            Court(id = 2, name = "Cancha Sur - Sintético", price = 22.5, imageUrl = DUMMY_IMAGE_ID, description = "Césped sintético de alta calidad."),
            Court(id = 3, name = "Cancha Valle - Sintético", price = 25.0, imageUrl = DUMMY_IMAGE_ID, description = "Cancha techada y climatizada."),
            Court(id = 4, name = "Cancha Pick - Baby Fut", price = 15.0, imageUrl = DUMMY_IMAGE_ID, description = "Especializada para baby fútbol."),
        )

        // 2. Reservas simuladas
        val mockReservations = mutableListOf(
            Reservation(id = 101, courtName = "Cancha Norte - Pasto Real", userName = "Juan Pérez", time = "Hoy, 22 Oct 20:00", status = "CONFIRMADA"),
            Reservation(id = 102, courtName = "Cancha Sur - Sintético", userName = "Ana López", time = "Mañana, 23 Oct 10:00", status = "PENDIENTE"),
            Reservation(id = 103, courtName = "Cancha Valle - Sintético", userName = "Carlos Ruiz", time = "Mañana, 23 Oct 18:00", status = "CONFIRMADA"),
            Reservation(id = 104, courtName = "Cancha Pick - Baby Fut", userName = "Elena Gómez", time = "Pasado, 24 Oct 14:00", status = "PENDIENTE"),
            Reservation(id = 105, courtName = "Cancha Norte - Pasto Real", userName = "David Solís", time = "Pasado, 24 Oct 21:00", status = "CANCELADA")
        )

        _state.update {
            it.copy(
                courtList = mockCourts,
                reservationList = mockReservations,
                isLoading = false
            )
        }
    }

// ----------------------------------------------------------------------
// LÓGICA DE GESTIÓN DE CANCHAS
// ----------------------------------------------------------------------

    fun addCourt(name: String, price: Double) {
        viewModelScope.launch {
            // Simular un ID único
            val newId = Random.nextInt(100, 1000)
            // CORRECCIÓN: Se añaden valores por defecto a los campos faltantes (imageUrl, description)
            val newCourt = Court(
                id = newId,
                name = name,
                price = price,
                imageUrl = DUMMY_IMAGE_ID,
                description = DEFAULT_DESCRIPTION
            )

            _state.update { currentState ->
                currentState.copy(courtList = currentState.courtList + newCourt)
            }
        }
    }

    fun editCourt(id: Int, newName: String, newPrice: Double) {
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedList = currentState.courtList.map { court ->
                    if (court.id == id) {
                        // Se actualiza nombre y precio (los demás campos se mantienen)
                        court.copy(name = newName, price = newPrice)
                    } else {
                        court
                    }
                }
                currentState.copy(courtList = updatedList)
            }
        }
    }

    fun deleteCourt(id: Int) {
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedList = currentState.courtList.filter { it.id != id }
                currentState.copy(courtList = updatedList)
            }
        }
    }

// ----------------------------------------------------------------------
// LÓGICA DE GESTIÓN DE RESERVAS
// ----------------------------------------------------------------------

    fun updateReservationStatus(id: Int, newStatus: String) {
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedList = currentState.reservationList.map { reservation ->
                    if (reservation.id == id) {
                        reservation.copy(status = newStatus)
                    } else {
                        reservation
                    }
                }
                currentState.copy(reservationList = updatedList)
            }
        }
    }


}