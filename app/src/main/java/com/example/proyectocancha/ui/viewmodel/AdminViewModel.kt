package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminState(
    val userList: List<UserEntity> = emptyList(),
    val courtList: List<CourtEntity> = emptyList(), // <-- Tipo actualizado a CourtEntity
    val isLoading: Boolean = true
)

class AdminViewModel(
    private val userRepository: UserRepository,
    private val courtRepository: CourtRepository // <-- Inyectamos CourtRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminState())
    val state = _state.asStateFlow()

    init {
        loadAdminData()
    }

    private fun loadAdminData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val users = userRepository.getAllUsers()
            val courts = courtRepository.getAllCourts() // <-- Obtenemos canchas de la BD
            _state.update { it.copy(userList = users, courtList = courts, isLoading = false) }
        }
    }

    fun toggleUserAdminStatus(userToUpdate: UserEntity) {
        viewModelScope.launch {
            val updatedUser = userToUpdate.copy(isAdmin = !userToUpdate.isAdmin)
            userRepository.updateUser(updatedUser)
            loadAdminData() // Recargamos para ver el cambio
        }
    }

    // --- Gestión de Canchas CONECTADA A LA BASE DE DATOS ---
    fun addCourt(name: String, price: Double, imageUrl: String, description: String) {
        viewModelScope.launch {
            val newCourt = CourtEntity(name = name, price = price, imageUrl = imageUrl, description = description)
            courtRepository.insertCourt(newCourt)
            loadAdminData()
        }
    }

    fun updateCourt(court: CourtEntity) {
        viewModelScope.launch {
            courtRepository.updateCourt(court)
            loadAdminData()
        }
    }

    fun deleteCourt(court: CourtEntity) {
        viewModelScope.launch {
            courtRepository.deleteCourt(court)
            loadAdminData()
        }
    }
}