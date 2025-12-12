package com.example.proyectocancha.data.repository

import com.example.proyectocancha.data.local.booking.BookingDao
import com.example.proyectocancha.data.local.booking.BookingEntity
import com.example.proyectocancha.data.local.user.AuthManager
import com.example.proyectocancha.data.local.user.UserEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BookingRepositoryTest {

    private lateinit var bookingDao: BookingDao
    private lateinit var bookingRepository: BookingRepository

    private val testUser = UserEntity(1, "Test User", "test@test.com", "12345678", "password", false)

    @Before
    fun setUp() {
        bookingDao = mockk(relaxed = true)
        bookingRepository = BookingRepository(bookingDao)
        mockkObject(AuthManager) // Mockk para el objeto AuthManager
    }

    @Test
    fun `getMyBookings con usuario logueado retorna sus reservas`() = runBlocking {
        val bookings = listOf(BookingEntity(1, 1, 1, "Cancha 1", "2024-01-01", "10:00", 15.0, "Activa"))
        coEvery { AuthManager.currentUser.value } returns testUser
        coEvery { bookingDao.getBookingsForUser(1) } returns bookings

        val result = bookingRepository.getMyBookings()

        assertEquals(1, result.size)
        assertEquals("Cancha 1", result[0].courtName)
    }

    @Test
    fun `getMyBookings con usuario no logueado retorna lista vacia`() = runBlocking {
        coEvery { AuthManager.currentUser.value } returns null

        val result = bookingRepository.getMyBookings()

        assertEquals(0, result.size)
    }

    @Test
    fun `addBooking con usuario logueado inserta una reserva`() = runBlocking {
        coEvery { AuthManager.currentUser.value } returns testUser

        bookingRepository.addBooking(1, "Cancha 1", "2024-01-01", "10:00", 15.0)

        coVerify { bookingDao.insert(any()) }
    }

    @Test
    fun `addBooking con usuario no logueado no inserta una reserva`() = runBlocking {
        coEvery { AuthManager.currentUser.value } returns null

        bookingRepository.addBooking(1, "Cancha 1", "2024-01-01", "10:00", 15.0)

        coVerify(exactly = 0) { bookingDao.insert(any()) }
    }

    @Test
    fun `cancelBooking llama a updateBookingStatus`() = runBlocking {
        bookingRepository.cancelBooking(1)

        coVerify { bookingDao.updateBookingStatus(1, "Cancelada") }
    }

    @Test
    fun `getMyActiveBookingCount con usuario logueado retorna el contador`() = runBlocking {
        coEvery { AuthManager.currentUser.value } returns testUser
        coEvery { bookingDao.getActiveBookingCount(1) } returns 5

        val result = bookingRepository.getMyActiveBookingCount()

        assertEquals(5, result)
    }

    @Test
    fun `getMyActiveBookingCount con usuario no logueado retorna 0`() = runBlocking {
        coEvery { AuthManager.currentUser.value } returns null

        val result = bookingRepository.getMyActiveBookingCount()

        assertEquals(0, result)
    }

    @Test
    fun `getBookedHoursForCourt retorna las horas ocupadas`() = runBlocking {
        val bookings = listOf(
            BookingEntity(1, 1, 1, "Cancha 1", "2024-01-01", "10:00", 15.0, "Activa"),
            BookingEntity(2, 1, 1, "Cancha 1", "2024-01-01", "11:00", 15.0, "Activa")
        )
        coEvery { bookingDao.getBookingsForCourtOnDay(1, "2024-01-01") } returns bookings

        val result = bookingRepository.getBookedHoursForCourt(1, "2024-01-01")

        assertEquals(listOf("10:00", "11:00"), result)
    }
}