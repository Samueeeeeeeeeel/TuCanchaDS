package com.example.proyectocancha.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingState(
    val bookings: List<BookingEntity> = emptyList(),
    val isLoading: Boolean = true,
    val activeCount: Int = 0,
    val availableTimes: List<String> = emptyList()
)

class BookingViewModel(private val repository: BookingRepository) : ViewModel() {

    private val _state = MutableStateFlow(BookingState())
    val state = _state.asStateFlow()

    private val _bookingResult = MutableStateFlow<Result<Unit>?>(null)
    val bookingResult = _bookingResult.asStateFlow()

    private val allPossibleTimes = (9..22).map { "$it:00 - ${it + 1}:00" }

    fun loadAllBookings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val bookings = repository.getMyBookings()
            val activeCount = repository.getMyActiveBookingCount()
            _state.update { it.copy(bookings = bookings, activeCount = activeCount, isLoading = false) }
        }
    }

    fun loadAvailableTimes(courtId: Int, day: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, availableTimes = emptyList()) }
            val bookedTimes = repository.getBookedHoursForCourt(courtId, day)
            val available = allPossibleTimes.filter { it !in bookedTimes }
            _state.update { it.copy(availableTimes = available, isLoading = false) }
        }
    }


    fun addBooking(courtId: Int, courtName: String, day: String, time: String, total: Double) {
        viewModelScope.launch {
            try {
                // La llamada al repositorio ahora es consistente
                repository.addBooking(courtId, courtName, day, time, total)
                _bookingResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _bookingResult.value = Result.failure(e)
            }
        }
    }

    fun cancelBooking(bookingId: Int) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
            loadAllBookings()
        }
    }

    fun clearResult() {
        _bookingResult.value = null
    }
}