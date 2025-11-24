package com.example.proyectocancha.ui.viewmodel

import com.example.proyectocancha.data.local.user.AuthManager
import com.example.proyectocancha.data.local.user.UserEntity
import com.example.proyectocancha.data.repository.UserRepository
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        userRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // Limpiamos los mocks estáticos para no afectar a otros tests
        unmockkAll()
    }

    @Test
    fun `init - cuando hay un usuario logueado - se carga su información`() {
        // Arrange
                val fakeUser = UserEntity(id = 1L, name = "Samuel Osorio", email = "samuel@osorio.com", phone = "+56912345678", password = "ValidTestPassword123!")
        // Mockeamos AuthManager para este test para aislarlo
        mockkObject(AuthManager)
        AuthManager.login(fakeUser)

        // Act
        viewModel = ProfileViewModel(userRepository)

        // Assert
        val state = viewModel.state.value
        assertEquals(fakeUser.name, state.nombre)
        assertEquals(fakeUser.email, state.email)
        assertEquals(fakeUser.phone, state.telefono)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `init - cuando no hay usuario logueado - el estado está vacío`() {
        // Arrange
        mockkObject(AuthManager)
        AuthManager.logout()

        // Act
        viewModel = ProfileViewModel(userRepository)

        // Assert
        val state = viewModel.state.value
        assertEquals("", state.nombre)
        assertEquals("", state.email)
        assertEquals("", state.telefono)
        assertEquals(false, state.isLoading)
    }

}
