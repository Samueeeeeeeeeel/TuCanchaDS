// ARCHIVO: AdminViewModel.kt
// UBICACIÓN: app/src/main/java/com/example/proyectocancha/ui/viewmodel/

package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.R
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.Reservation // <-- Asegúrate de importar tu modelo Reservation
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
    val reservationList: List<Reservation> = emptyList(), // <-- Debe ser List<Reservation>
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state

    // ID de recurso dummy para la imagen de la cancha
    private val DUMMY_IMAGE_ID = 0 // Puedes cambiarlo por R.drawable.court_1 si existe
    private val DEFAULT_DESCRIPTION = "Cancha estándar administrable."

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _state.update { it.copy(isLoading = true) }

        // 1. Canchas simuladas (Esto estaba bien)
        val mockCourts = mutableListOf(
            Court(1, "Cancha Norte - Pasto Real", 20000, R.drawable.court_1,
                "Cancha con excelentes instalaciones y ambiente familiar."),
            Court(2, "Cancha Sur - Sintético", 22500, R.drawable.court_2,
                "Césped sintético de alta calidad, ideal para juegos rápidos y ligeros, techado."),
            Court(3, "Doble Cancha - Sintético", 30000, R.drawable.court_3
                , "Doble Cancha para competencias Amateurs, Excelentes luces para jugar en cualquier momento."),
            Court(4, "Cancha Pick - Baby Fut", 15000, R.drawable.court_4,
                "Cancha pequeña con iluminación profesional, especializada para baby fútbol."),
            Court(5, "Cancha Premium - VIP", 35000, R.drawable.court_5,
                "Cancha con vestuarios de lujo y servicio exclusivo."),
            Court(6, "Cancha Express - Rápida", 18000, R.drawable.court_6,
                "Cancha ideal para reservas de última hora.")
        ) // <-- Cierre de la lista de canchas

        // 2. Reservas simuladas (¡¡¡ESTA ES LA CORRECCIÓN!!!)
        //    Se crea una lista de 'Reservation' en lugar de 'Court'
        //    y se añade el ')' que faltaba al final.
        //    (Ajusta los campos si tu clase Reservation es diferente)
        val mockReservations = mutableListOf(
            Reservation(
                id = 101,
                courtName = "Cancha Norte - Pasto Real",
                time = "18:00",
                status = "Confirmada",
                userName = "usuario_1"
            ),
            Reservation(
                id = 102,
                courtName = "Cancha Sur - Sintético",
                time = "19:00",
                status = "Pendiente",
                userName = "usuario_2"
            ),
            Reservation(
                id = 103,
                courtName = "Cancha Norte - Pasto Real",
                time = "10:00",
                status = "Cancelada",
                userName = "usuario_3"
            )
        )// <-- ¡Este ')' faltaba en tu código original!

        // Esta actualización ahora funcionará correctamente
        _state.update {
            it.copy(
                courtList = mockCourts,
                reservationList = mockReservations, // <-- Ahora los tipos coinciden
                isLoading = false
            )
        }
    }

// ----------------------------------------------------------------------
// LÓGICA DE GESTIÓN DE CANCHAS (SIN CAMBIOS, TAL COMO LO PEDISTE)
// ----------------------------------------------------------------------

    fun addCourt(name: String, price: Int) {
        viewModelScope.launch {
            // Simular un ID único
            val newId = Random.nextInt(100, 1000)
            // CORRECCIÓN: Se añaden valores por defecto a los campos faltantes (imageUrl, description)
            val newCourt = Court(
                id = newId,
                name = name,
                price = price,
                imageUrl = DUMMY_IMAGE_ID, // <-- Tu variable
                description = DEFAULT_DESCRIPTION // <-- Tu variable
            )

            _state.update { currentState ->
                currentState.copy(courtList = currentState.courtList + newCourt)
            }
        }
    }

    fun editCourt(id: Int, newName: String, newPrice: Int) {
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
// LÓGICA DE GESTIÓN DE RESERVAS (SIN CAMBIOS)
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