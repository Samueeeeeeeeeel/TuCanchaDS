package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.local.user.AuthManager
import com.example.proyectocancha.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingUiState(
    val bookings: List<BookingEntity> = emptyList(),
    val activeCount: Int = 0,
    val lastBooking: BookingEntity? = null,
    val isLoading: Boolean = true
)

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingUiState())
    val state: StateFlow<BookingUiState> = _state.asStateFlow()

    init {
        observeBookingsForCurrentUser()
    }

    private fun observeBookingsForCurrentUser() {
        viewModelScope.launch {
            AuthManager.currentUser.collect { user ->
                if (user == null) {
                    _state.value = BookingUiState(isLoading = false)
                } else {
                    repository
                        .getBookingsForUser(user.id)
                        .combine(repository.getActiveCountForUser(user.id)) { bookings, count ->
                            BookingUiState(
                                bookings = bookings,
                                activeCount = count,
                                lastBooking = bookings.maxByOrNull { it.id },
                                isLoading = false
                            )
                        }
                        .collect { ui ->
                            _state.value = ui
                        }
                }
            }
        }
    }

    fun createBooking(
        courtName: String,
        day: String,
        time: String,
        subtotal: Double,
        fee: Double,
        total: Double
    ) {
        viewModelScope.launch {
            val user = AuthManager.currentUser.value ?: return@launch

            val booking = BookingEntity(
                userId = user.id,
                courtName = courtName,
                day = day,
                time = time,
                subtotal = subtotal,
                fee = fee,
                total = total,
                status = "Activa"
            )

            repository.createBooking(booking)
            // No hace falta actualizar lastBooking aquí, la DB se recarga sola
        }
    }

    fun cancelBooking(bookingId: Long) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
        }
    }
}
