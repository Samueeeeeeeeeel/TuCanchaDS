package com.example.proyectocancha.ui.viewmodel

// UBICACIÓN: app/src/main/java/com.example.proyectocancha.ui.viewmodel/AppViewModelFactory.kt



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.proyectocancha.data.repository.CourtRepository
import com.example.uinavegacion.data.repository.UserRepository


// Archivo: AppViewModelFactory.kt

class AppViewModelFactory(
    // CAMBIO: Usamos userRepository para coincidir con el nombre que usas en MainActivity
    private val userRepository: UserRepository,
    private val courtRepository: CourtRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Actualiza la lógica de creación para usar 'userRepository'
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            // Si el ViewModel solicitado no está en la lista
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    /**
     * Función que crea la Factory para inyectar el courtId en CourtDetailViewModel.
     * Esto se usa directamente en el NavGraph.
     */
    fun createCourtDetailFactory(courtId: Int): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CourtDetailViewModel::class.java)) {
                    // Crea CourtDetailViewModel pasándole el ID correcto y el Repository
                    return CourtDetailViewModel(courtRepository, courtId) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class for CourtDetail")
            }
        }
    }
}