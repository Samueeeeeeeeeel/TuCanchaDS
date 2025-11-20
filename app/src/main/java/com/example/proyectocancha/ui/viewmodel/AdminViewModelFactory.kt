package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.repository.UserRepository

class AdminViewModelFactory(
    private val userRepository: UserRepository,
    private val courtRepository: CourtRepository // <-- AÑADIMOS CourtRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Le pasamos ambos repositorios al ViewModel
            return AdminViewModel(userRepository, courtRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
