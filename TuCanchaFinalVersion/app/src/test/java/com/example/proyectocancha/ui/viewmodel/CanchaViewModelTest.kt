package com.example.proyectocancha.ui.viewmodel

import com.example.proyectocancha.data.local.court.CourtEntity
import com.example.proyectocancha.data.local.court.CourtRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CanchaViewModelTest {

    private lateinit var courtRepository: CourtRepository
    private lateinit var viewModel: CanchaViewModel

    @Before
    fun setUp() {
        courtRepository = mockk(relaxed = true)
        viewModel = CanchaViewModel(courtRepository)
    }

    @Test
    fun `loadAllCourts - carga la lista de canchas correctamente`() = runTest {
        // Arrange: Preparamos la respuesta del repositorio
        val fakeCourts = listOf(
            CourtEntity(id = 1, name = "Cancha 1", price = 100.0, "", ""),
            CourtEntity(id = 2, name = "Cancha 2", price = 150.0, "", "")
        )
        coEvery { courtRepository.getAllCourts() } returns fakeCourts

        // Act: Llamamos a la función
        viewModel.loadAllCourts()

        // Assert: Verificamos que el estado se actualizó
        val state = viewModel.state.value
        assertEquals(fakeCourts, state.courtList)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `loadCourtById - carga la cancha correcta`() = runTest {
        // Arrange
        val fakeCourt = CourtEntity(id = 5, name = "Cancha Específica", price = 200.0, "", "")
        coEvery { courtRepository.getCourtById(5) } returns fakeCourt

        // Act
        viewModel.loadCourtById(5)

        // Assert
        val state = viewModel.state.value
        assertEquals(fakeCourt, state.selectedCourt)
        assertEquals(false, state.isLoading)
    }
}
