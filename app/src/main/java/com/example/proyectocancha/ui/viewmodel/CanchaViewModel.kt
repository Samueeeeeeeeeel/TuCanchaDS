package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.court.CourtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado para las pantallas de canchas del usuario
data class CanchaState(
    val courtList: List<CourtEntity> = emptyList(),
    val selectedCourt: CourtEntity? = null,
    val isLoading: Boolean = true
)

class CanchaViewModel(private val courtRepository: CourtRepository) : ViewModel() {

    private val _state = MutableStateFlow(CanchaState())
    val state = _state.asStateFlow()

    // Carga todas las canchas para las listas
    fun loadAllCourts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val courts = courtRepository.getAllCourts()
            _state.update { it.copy(courtList = courts, isLoading = false) }
        }
    }

    // Carga una cancha específica para la pantalla de detalles
    fun loadCourtById(courtId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, selectedCourt = null) } // Resetea la cancha seleccionada
            val court = courtRepository.getCourtById(courtId)
            _state.update { it.copy(selectedCourt = court, isLoading = false) }
        }
    }
}
