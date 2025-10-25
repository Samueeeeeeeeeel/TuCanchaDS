// UBICACIÓN: app/src/main/java/com/example/proyectocancha/ui/viewmodel/AdminViewModel.kt

package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.AdminRepository // <--- ¡DEBES CREAR ESTA INTERFAZ!
import com.example.proyectocancha.ui.model.Court
import com.example.proyectocancha.ui.model.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ----------------------------------------------------------------------
// ESTADO DE LA UI DE ADMINISTRACIÓN (NO CAMBIA)
// ----------------------------------------------------------------------

data class AdminUiState(
    val courtList: List<Court> = emptyList(),
    val reservationList: List<Reservation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ----------------------------------------------------------------------
// ADMIN VIEW MODEL (REFRACTORIZADO)
// ----------------------------------------------------------------------

class AdminViewModel(
    private val repository: AdminRepository // <--- Ahora se inyecta el Repositorio
) : ViewModel() {

    private val _state = MutableStateFlow(AdminUiState())
    val state: StateFlow<AdminUiState> = _state

    init {
        // Al iniciar, cargamos los datos reales desde la base de datos a través del Repository
        loadDataFromRepository()
    }

    // NUEVA FUNCIÓN: Carga los datos desde la capa de datos
    fun loadDataFromRepository() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // 1. Obtener canchas a través del Repository
                val courts = repository.getAllCourts()
                // 2. Obtener reservas a través del Repository
                val reservations = repository.getAllReservations()

                _state.update {
                    it.copy(
                        courtList = courts,
                        reservationList = reservations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Manejo de errores de conexión/base de datos
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar datos: ${e.message}"
                    )
                }
            }
        }
    }


    // ----------------------------------------------------------------------
    // LÓGICA DE GESTIÓN DE CANCHAS (DELEGA AL REPOSITORY)
    // ----------------------------------------------------------------------

    fun addCourt(name: String, price: Int) {
        viewModelScope.launch {
            val newCourt = Court(
                id = 0, // El ID lo generará la DB
                name = name,
                price = price,
                imageUrl = 0,
                description = "Cancha estándar administrable."
            )
            repository.addCourt(newCourt) // <--- ¡DELEGA LA ACCIÓN!
            loadDataFromRepository() // Recarga los datos para actualizar la UI
        }
    }

    fun editCourt(id: Int, newName: String, newPrice: Int) {
        viewModelScope.launch {
            repository.updateCourt(id, newName, newPrice) // <--- ¡DELEGA LA ACCIÓN!
            loadDataFromRepository() // Recarga
        }
    }

    fun deleteCourt(id: Int) {
        viewModelScope.launch {
            repository.deleteCourt(id) // <--- ¡DELEGA LA ACCIÓN!
            loadDataFromRepository() // Recarga
        }
    }

    // ----------------------------------------------------------------------
    // LÓGICA DE GESTIÓN DE RESERVAS (DELEGA AL REPOSITORY)
    // ----------------------------------------------------------------------

    fun updateReservationStatus(id: Int, newStatus: String) {
        viewModelScope.launch {
            repository.updateReservationStatus(id, newStatus) // <--- ¡DELEGA LA ACCIÓN!
            loadDataFromRepository() // Recarga
        }
    }
}