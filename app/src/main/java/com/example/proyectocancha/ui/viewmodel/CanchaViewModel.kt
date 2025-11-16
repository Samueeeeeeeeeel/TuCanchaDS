package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.court.CourtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CanchaState(
    val courtList: List<CourtEntity> = emptyList(),
    val isLoading: Boolean = true
)

class CanchaViewModel(private val courtRepository: CourtRepository) : ViewModel() {

    private val _state = MutableStateFlow(CanchaState())
    val state = _state.asStateFlow()

    init {
        loadCourts()
    }

    private fun loadCourts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val courts = courtRepository.getAllCourts()
            _state.update { it.copy(courtList = courts, isLoading = false) }
        }
    }
}
