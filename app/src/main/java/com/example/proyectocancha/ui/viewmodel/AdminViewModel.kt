package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado para la pantalla de Admin
data class AdminState(
    val userList: List<UserEntity> = emptyList(),
    val isLoading: Boolean = true
)

class AdminViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow(AdminState())
    val state = _state.asStateFlow()

    init {
        loadUsers()
    }

    // Carga todos los usuarios desde el repositorio
    private fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val users = userRepository.getAllUsers()
            _state.update { it.copy(userList = users, isLoading = false) }
        }
    }
}
