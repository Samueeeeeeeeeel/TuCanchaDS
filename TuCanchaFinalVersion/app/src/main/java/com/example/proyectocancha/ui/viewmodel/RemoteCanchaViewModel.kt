package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.remote.CanchaApi
import com.example.proyectocancha.data.remote.dto.CanchaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RemoteCanchaState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val canchasList: List<CanchaDto> = emptyList(),
    val selectedCancha: CanchaDto? = null
)

class RemoteCanchaViewModel(
    private val canchaApi: CanchaApi
) : ViewModel() {

    private val _state = MutableStateFlow(RemoteCanchaState())
    val state = _state.asStateFlow()

    fun loadCanchasActivas() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = canchaApi.obtenerCanchasActivas()
                if (response.isSuccessful && response.body() != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            canchasList = response.body()!!,
                            errorMsg = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al cargar canchas: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al cargar canchas"
                    )
                }
            }
        }
    }

    fun loadTodasLasCanchas() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = canchaApi.obtenerTodasLasCanchas()
                if (response.isSuccessful && response.body() != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            canchasList = response.body()!!,
                            errorMsg = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al cargar canchas: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al cargar canchas"
                    )
                }
            }
        }
    }

    fun loadCanchaById(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null, selectedCancha = null) }
            try {
                val response = canchaApi.obtenerCanchaPorId(id)
                if (response.isSuccessful && response.body() != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            selectedCancha = response.body()!!,
                            errorMsg = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al cargar cancha: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al cargar cancha"
                    )
                }
            }
        }
    }

    fun crearCancha(cancha: CanchaDto) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = canchaApi.crearCancha(cancha)
                if (response.isSuccessful && response.body() != null) {
                    // Recargar la lista después de crear
                    loadTodasLasCanchas()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al crear cancha: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al crear cancha"
                    )
                }
            }
        }
    }

    fun actualizarCancha(id: Long, cancha: CanchaDto) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = canchaApi.actualizarCancha(id, cancha)
                if (response.isSuccessful) {
                    // Recargar la lista después de actualizar
                    loadTodasLasCanchas()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al actualizar cancha: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al actualizar cancha"
                    )
                }
            }
        }
    }

    fun eliminarCancha(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                val response = canchaApi.eliminarCancha(id)
                if (response.isSuccessful) {
                    // Recargar la lista después de eliminar
                    loadTodasLasCanchas()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMsg = "Error al eliminar cancha: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = e.message ?: "Error desconocido al eliminar cancha"
                    )
                }
            }
        }
    }
}

