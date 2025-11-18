package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookingState(
    val bookings: List<BookingEntity> = emptyList(),
    val isLoading: Boolean = true
)

class BookingViewModel(
    private val repository: BookingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingState())
    val state = _state.asStateFlow()

    fun loadUserBookings(email: String) {
        viewModelScope.launch {
            _state.value = BookingState(isLoading = true)
            val list = repository.getUserBookings(email)
            _state.value = BookingState(bookings = list, isLoading = false)
        }
    }

    fun createBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.insert(booking)
        }
    }
}
