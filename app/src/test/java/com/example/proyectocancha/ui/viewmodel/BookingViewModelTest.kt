package com.example.proyectocancha.ui.viewmodel

import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.repository.BookingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BookingViewModelTest {

    private lateinit var bookingRepository: BookingRepository
    private lateinit var viewModel: BookingViewModel

    @Before
    fun setUp() {
        bookingRepository = mockk(relaxed = true)
        viewModel = BookingViewModel(bookingRepository)
    }

    @Test
    fun `loadAllBookings - carga las reservas y el contador`() = runTest {
        // Arrange
        val fakeBookings = listOf(BookingEntity(id = 1, userId = 1, courtId = 1, courtName = "Test", day = "01/01/2025", time = "10:00 - 11:00", total = 100.0))
        coEvery { bookingRepository.getMyBookings() } returns fakeBookings
        coEvery { bookingRepository.getMyActiveBookingCount() } returns 1

        // Act
        viewModel.loadAllBookings()

        // Assert
        val state = viewModel.state.value
        assertEquals(fakeBookings, state.bookings)
        assertEquals(1, state.activeCount)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `addBooking - con datos válidos - el resultado es exitoso`() = runTest {
        // Arrange

        // Act
        viewModel.addBooking(courtId = 1, courtName = "Cancha Test", day = "01/01/2025", time = "10:00 - 11:00", total = 100.0)

        // Assert
        coVerify { bookingRepository.addBooking(1, "Cancha Test", "01/01/2025", "10:00 - 11:00", 100.0) }
        assertTrue(viewModel.bookingResult.value?.isSuccess == true)
    }

    @Test
    fun `cancelBooking - llama al repositorio y recarga la lista`() = runTest {
        // Act
        viewModel.cancelBooking(1)

        // Assert
        // --- ¡TEST CORREGIDO Y COMPLETO! ---
        // Ahora verificamos las 3 acciones que realiza la función
        coVerify { bookingRepository.cancelBooking(1) }
        coVerify { bookingRepository.getMyBookings() }
        coVerify { bookingRepository.getMyActiveBookingCount() } // <-- VERIFICACIÓN AÑADIDA
    }

}