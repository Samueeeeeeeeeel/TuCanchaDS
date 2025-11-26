package com.example.proyectocancha.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.repository.UserRepository

class AdminViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val courtRepository: CourtRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(application, userRepository, courtRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
