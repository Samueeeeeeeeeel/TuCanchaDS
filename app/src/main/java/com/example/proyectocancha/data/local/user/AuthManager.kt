package com.example.proyectocancha.data.local.user

import com.example.proyectocancha.data.local.user.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthManager {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun login(user: UserEntity) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateUser(updatedUser: UserEntity) {
        _currentUser.value = updatedUser
    }
}
