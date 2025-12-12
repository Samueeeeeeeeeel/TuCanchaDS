package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectocancha.data.local.court.CourtRepository

// Factory para el CanchaViewModel. Su único trabajo es pasarle el CourtRepository.
class CanchaViewModelFactory(private val courtRepository: CourtRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CanchaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CanchaViewModel(courtRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
