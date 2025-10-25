package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.R
import com.example.proyectocancha.data.repository.CourtRepository
import com.example.proyectocancha.ui.model.Court
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CourtDetailUiState(
    val court: Court = Court( // Inicializa con un objeto "vacío" o "no encontrado"
        id = 0,
        name = "Cargando...",
        price = 0,
        imageUrl = R.drawable.court_1, // Usa un placeholder de imagen
        description = ""
    ),
    val isLoading: Boolean = true,
    val error: String? = null
)

class CourtDetailViewModel(
    private val repository: CourtRepository, // Inyección del Repositorio
    private val courtId: Int // El ID se inyectará en la Factory
) : ViewModel() {

    private val _state = MutableStateFlow(CourtDetailUiState())
    val state: StateFlow<CourtDetailUiState> = _state

    init {
        loadCourtDetails(courtId)
    }

    private fun loadCourtDetails(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // LLAMA AL REPOSITORY para buscar la cancha
                val courtResult = repository.getCourtById(id)

                _state.update {
                    it.copy(
                        court = courtResult ?: it.court.copy(name = "Cancha No Encontrada"),
                        isLoading = false,
                        error = if (courtResult == null) "Cancha no disponible" else null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error al cargar: ${e.message}") }
            }
        }
    }
}