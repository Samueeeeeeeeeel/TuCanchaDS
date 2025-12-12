package com.example.proyectocancha.ui.viewmodel

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.proyectocancha.data.local.court.CourtRepository
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class AdminViewModelTest {

    private lateinit var viewModel: AdminViewModel
    private val userRepository: UserRepository = mockk(relaxed = true)
    private val courtRepository: CourtRepository = mockk(relaxed = true)
    private val application: Application = ApplicationProvider.getApplicationContext()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { userRepository.getAllUsers() } returns listOf(UserEntity(1, "Test User", "test@user.com", "12345678", "pass", false))
        coEvery { courtRepository.getAllCourts() } returns emptyList()

        viewModel = AdminViewModel(application, userRepository, courtRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init - carga usuarios y canchas y finaliza la carga`() = runTest {
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(1, state.userList.size)
        assertEquals("Test User", state.userList[0].name)
    }

    @Test
    fun `toggleUserAdminStatus - actualiza el usuario y recarga los datos`() = runTest {
        val userToToggle = UserEntity(1, "Test User", "test@user.com", "12345678", "pass", false)

        viewModel.toggleUserAdminStatus(userToToggle)

        // ¡ARREGLO! Esperamos a que la coroutina termine
        advanceUntilIdle()

        coVerify { userRepository.updateUser(userToToggle.copy(isAdmin = true)) }
        coVerify { userRepository.getAllUsers() }
        coVerify { courtRepository.getAllCourts() }
    }

    @Test
    fun `deleteCourt - llama a deleteCourt del repositorio y recarga los datos`() = runTest {
        val courtToDelete = com.example.proyectocancha.data.local.court.CourtEntity(id = 1, name = "Cancha a borrar", price = 10000.0, description = "desc", imageUrl = "")

        viewModel.deleteCourt(courtToDelete)

        // ¡ARREGLO! Esperamos a que la coroutina termine
        advanceUntilIdle()

        coVerify { courtRepository.deleteCourt(courtToDelete) }
        coVerify { courtRepository.getAllCourts() }
    }
}