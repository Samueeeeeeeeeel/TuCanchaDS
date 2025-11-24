package com.example.proyectocancha.ui.viewmodel

import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.repository.BookingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        bookingRepository = mockk(relaxed = true)
        viewModel = BookingViewModel(bookingRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAllBookings - carga las reservas y el contador`() = runTest {
        val fakeBookings = listOf(BookingEntity(id = 1, userId = 1, courtId = 1, courtName = "Test", day = "01/01/2025", time = "10:00 - 11:00", total = 100.0))
        coEvery { bookingRepository.getMyBookings() } returns fakeBookings
        coEvery { bookingRepository.getMyActiveBookingCount() } returns 1

        viewModel.loadAllBookings()
        advanceUntilIdle() // Esperamos a que la coroutina termine

        val state = viewModel.state.value
        assertEquals(fakeBookings, state.bookings)
        assertEquals(1, state.activeCount)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `addBooking - con datos válidos - el resultado es exitoso`() = runTest {
        viewModel.addBooking(courtId = 1, courtName = "Cancha Test", day = "01/01/2025", time = "10:00 - 11:00", total = 100.0)
        advanceUntilIdle() // Esperamos a que la coroutina termine

        coVerify { bookingRepository.addBooking(1, "Cancha Test", "01/01/2025", "10:00 - 11:00", 100.0) }
        assertTrue(viewModel.bookingResult.value?.isSuccess == true)
    }

    @Test
    fun `addBooking - cuando el repositorio falla - el resultado es de error`() = runTest {
        // Arrange: Simulamos que el repositorio lanza una excepción
        val errorMessage = "Error en la base de datos"
        coEvery { bookingRepository.addBooking(any(), any(), any(), any(), any()) } throws Exception(errorMessage)

        // Act
        viewModel.addBooking(courtId = 1, courtName = "Cancha Test", day = "01/01/2025", time = "10:00 - 11:00", total = 100.0)
        advanceUntilIdle() // Esperamos a que la coroutina termine

        // Assert
        assertTrue(viewModel.bookingResult.value?.isFailure == true)
        assertEquals(errorMessage, viewModel.bookingResult.value?.exceptionOrNull()?.message)
    }

    @Test
    fun `cancelBooking - llama al repositorio y recarga la lista`() = runTest {
        viewModel.cancelBooking(1)
        advanceUntilIdle() // Esperamos a que la coroutina termine

        coVerify { bookingRepository.cancelBooking(1) }
        coVerify { bookingRepository.getMyBookings() }
        coVerify { bookingRepository.getMyActiveBookingCount() }
    }

}