package com.example.proyectocancha.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class AdminState(
    val userList: List<UserEntity> = emptyList(),
    val courtList: List<CourtEntity> = emptyList(),
    val isLoading: Boolean = true
)

class AdminViewModel(
    private val application: Application,
    private val userRepository: UserRepository,
    private val courtRepository: CourtRepository
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
            val courts = courtRepository.getAllCourts()
            _state.update { it.copy(userList = users, courtList = courts, isLoading = false) }
        }
    }

    fun toggleUserAdminStatus(userToUpdate: UserEntity) {
        viewModelScope.launch {
            val updatedUser = userToUpdate.copy(isAdmin = !userToUpdate.isAdmin)
            userRepository.updateUser(updatedUser)
            loadAdminData()
        }
    }

    private suspend fun saveImageToInternalStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        val context = application.applicationContext
        val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
        val directory = File(context.filesDir, "court_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val imageFile = File(directory, "${System.currentTimeMillis()}.jpg")

        FileOutputStream(imageFile).use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        imageFile.absolutePath
    }

    fun addCourt(name: String, price: Double, imageUri: String?, description: String) {
        viewModelScope.launch {
            val finalImagePath = imageUri?.let { saveImageToInternalStorage(Uri.parse(it)) } ?: ""
            val newCourt = CourtEntity(name = name, price = price, imageUrl = finalImagePath, description = description)
            courtRepository.insertCourt(newCourt)
            loadAdminData()
        }
    }

    fun updateCourt(court: CourtEntity, newImageUri: String?) {
        viewModelScope.launch {
            val finalImagePath = if (newImageUri != null) {
                saveImageToInternalStorage(Uri.parse(newImageUri))
            } else {
                court.imageUrl
            }
            val updatedCourt = court.copy(imageUrl = finalImagePath ?: court.imageUrl)
            courtRepository.updateCourt(updatedCourt)
            loadAdminData()
        }
    }

    fun deleteCourt(court: CourtEntity) {
        viewModelScope.launch {
            if (court.imageUrl.isNotEmpty()) {
                try {
                    File(court.imageUrl).delete()
                } catch (e: Exception) {
                }
            }
            courtRepository.deleteCourt(court)
            loadAdminData()
        }
    }
}
